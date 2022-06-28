# SCC Provider

## Note

If the project was generated via `start.spring.io`, note that the default configuration generated is wrong. See [#938](https://github.com/spring-io/start.spring.io/issues/938)

Change from

```groovy
tasks.named("contracts") {
    testFramework = org.springframework.cloud.contract.verifier.config.TestFramework.JUNIT5
}
```

to

```groovy
contracts {
    testFramework = org.springframework.cloud.contract.verifier.config.TestFramework.JUNIT5
    // add this to allow build when no contract is written yet
    failOnNoContracts = false
}
```

## Configuration

*** https://docs.spring.io/spring-cloud-contract/docs/current/reference/html/gradle-project.html#gradle-add-stubs

- Create the contract in `/test/resources/contracts/` which can be in `groovy, java, yaml, kotlin`
  - Choose `groovy` as it seem the most friendly out of all to write the contract in
- Create Base class in `/test/java/com/bwgjoseph/sccprovider/contracts`.
  - The base class and contract is used to define the testing behavior
- Define the base class in `build.gradle` under `contracts` extension
  - `baseClassForTests = 'com.bwgjoseph.sccprovider.contracts.BaseProfileContractTest'`
- Run `./gradlew generateContractTests` to generate the contract
  - By default, it will be invoked before `check` task
  - This command takes the contract file (e.g `getAllProfilesById.groovy`) and generates `ContractVerifierTest.java` file where the assertion is based on the input from the contract file
  - Note that it has nothing to do with the `mocks` defined in `BaseProfileContractTest.java`. The `mocks` is used in `contractTest` task as we will see later
  - Also note that this will generate a `mappings` in `/build/stubs` directory
    - This provides the "response" when run on `consumer` as a input to the `wiremock`
- Run `./gradlew contractTest` to run the test against the contract we defined
  - This will trigger the generated test class to run and verify against the actual method if we do not provide `mocks`
  - Hence, a call to `/profiles` will trigger the actual method call to the `ProfileController` then to the `ProfileService` before returning the value
  - This would almost certainly fail, since in real world scenario, this would grab the value from `database`, and verify against the one we wrote in the contract manually
  - So to overcome this, we introduce `mocks` to return the dataset we would expect in `BaseProfileContractTest`
  - That way, whenever, this test is triggered, the data returned would match exactly what is written in the contract
- However, in order for `Consumer` to consume the `contract`, we need a way to publish the `stub` for consume to "consume".
  - To do so, we will add [maven-publish](https://docs.gradle.org/current/userguide/publishing_maven.html) plugin
    - We only publish to local `.m2/repository` but if there is a remote repository available, it can be published there as well. Publishing to local just makes our development easier
  - Configure this
    ```groovy
    publishing {
        publications {
            mavenJava(MavenPublication) {
                // Refer to https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#publishing-your-application.maven-publish
                artifact bootJar
                // See https://docs.spring.io/spring-cloud-contract/docs/current/reference/html/gradle-project.html#gradle-publishing-stubs-to-artifact-repo
                artifact verifierStubsJar
            }
        }
    }
    ```
  - And run `./gradlew publishToMavenLocal`
  - Once completed, you will be able to find a local copy of the app and stub in `%USERPROFILE%/.m2/repository` directory

## Known Issue

### Deserialization mismatch

> Skipped testing for date, see [scc-localdatetime-assertions-fail](https://stackoverflow.com/questions/60550853/spring-cloud-contract-localdatetime-assertions-fail)

```
ContractVerifierTest > validate_getAllProfiles() FAILED
    java.lang.IllegalStateException: Parsed JSON [[{"id":1,"name":"Joseph","age":22,"email":"jose@gmail.com","dob":[2000,1,1]}]] doesn't match the JSON path [$[?(@.['dob'] == '2000-1-1')]]
        at com.toomuchcoding.jsonassert.JsonAsserter.check(JsonAsserter.java:228)
        at com.toomuchcoding.jsonassert.JsonAsserter.checkBufferedJsonPathString(JsonAsserter.java:267)
        at com.toomuchcoding.jsonassert.JsonAsserter.isEqualTo(JsonAsserter.java:101)
        at com.bwgjoseph.sccprovider.contracts.ContractVerifierTest.validate_getAllProfiles(ContractVerifierTest.java:39)
```

Above is if using fixed defined date

---

In the `contract`, we define `anyDate() / isoDate()` but in our class, the date itself is a `LocalDate` object.

```groovy
body (
    id: 1,
    name: "Joseph",
    age: 22,
    // provides useful pre-defined regex
    email: $(email())
    dob: $(anyDate())
)
```

```java
public class Profile {
    private int id;
    private String name;
    private int age;
    private String email;
    private LocalDate dob;
}
```

```java
// mock in BaseProfileContractTest
List<Profile> profiles = List.of(
    Profile.builder().id(1).name("Joseph").age(22).email("jose@gmail.com").dob(LocalDate.of(2000, 1, 1)).build(),
    Profile.builder().id(2).name("Sam").age(32).email("sam@gmail.com").dob(LocalDate.of(2000, 1, 1)).build()
);
```

```java
// generated verifier class
assertThatJson(parsedJson).field("['id']").isEqualTo(1);
assertThatJson(parsedJson).field("['name']").isEqualTo("Joseph");
assertThatJson(parsedJson).field("['age']").isEqualTo(22);
assertThatJson(parsedJson).field("['email']").matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
assertThatJson(parsedJson).field("['dob']").matches("(\\d\\d\\d\\d)-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])");
```

Error output

```log
java.lang.IllegalStateException: Parsed JSON [{"id":1,"name":"Joseph","age":22,"email":"jose@gmail.com","dob":[2000,1,1]}] doesn't match the JSON path [$[?(@.['dob'] =~ /(\d\d\d\d)-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])/)]]
	at com.toomuchcoding.jsonassert.JsonAsserter.check(JsonAsserter.java:228)
```

As fast as I understand, the JSON when parsed, is parsed into `[yyyy-mm-dd]` format which is quite different from the expected regex output from `anyDate()` which seem to accept `yyyy-mm-dd` instead.

How to resolve?

It seem that the root cause of this is the use of `standaloneSetup` vs `webAppContextSetup` where in short, `standalone` does not provide any context configure (ie. `JacksonMapping` for one), and `webAppContextSetup` provides the full context.

This would work

```java
public abstract class BaseProfileContractTest {
    // @Autowired
    // private ProfileController profileController;

    // inject WebApplicationContext
    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void beforeEach() {
        // switch from
        // RestAssuredMockMvc.standaloneSetup(profileController);

        // to this
        RestAssuredMockMvc.webAppContextSetup(context);
    }
}
```

But this brings in more context than we need, for example, we want to target only the `ProfileController`. So is there any way to bring in the `JacksonMapping` configuration with a `standaloneSetup`?

Turns out we can

```java
public abstract class BaseProfileContractTest {
    @Autowired
    private ProfileController profileController;

    // Grab the objectmapper from Spring
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void beforeEach() {

        RestAssuredMockMvc.standaloneSetup(
                MockMvcBuilders
                    .standaloneSetup(profileController)
                    // then configure it here
                    // this will ensure the consistent mapping behavior
                    .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                );
    }
}
```

References to this:

- https://stackoverflow.com/questions/46517559/spring-cloud-contract-dsl-verify-date-format
- https://stackoverflow.com/questions/54215875/how-to-set-response-date-format-in-rest-assured-mock-mvc-while-testing-spring-co
- https://medium.com/@vicusbass/datetime-serialization-in-spring-tests-5d31ccd025c
- https://stackoverflow.com/questions/31883657/customized-objectmapper-not-used-in-test
- https://piotrminkowski.wordpress.com/2017/04/26/testing-java-microservices/

### Generated ContractVerifierTest class excludes fields for assertion

See https://github.com/spring-cloud/spring-cloud-contract/issues/1803

## Tips

### Multiple ways to define the body

There are at least 2 ways you can define the body in the contract.

- Default
  - Write the body as is
- File
  - Pass a reference to a JSON file

> See `getAllProfiles.groovy` for an example