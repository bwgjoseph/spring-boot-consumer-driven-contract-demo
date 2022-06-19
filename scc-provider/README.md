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
  - `baseClassForTests = 'com.bwgjoseph.sccprovider.contracts.ContractsBase'`
- Run `./gradlew generateContractTests` to generate the contract
  - By default, it will be invoked before `check` task
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


> Skipped testing for date, see [scc-localdatetime-assertions-fail](https://stackoverflow.com/questions/60550853/spring-cloud-contract-localdatetime-assertions-fail)

```
ContractVerifierTest > validate_getAllProfiles() FAILED
    java.lang.IllegalStateException: Parsed JSON [[{"id":1,"name":"Joseph","age":22,"email":"jose@gmail.com","dob":[2000,1,1]}]] doesn't match the JSON path [$[?(@.['dob'] == '2000-1-1')]]
        at com.toomuchcoding.jsonassert.JsonAsserter.check(JsonAsserter.java:228)
        at com.toomuchcoding.jsonassert.JsonAsserter.checkBufferedJsonPathString(JsonAsserter.java:267)
        at com.toomuchcoding.jsonassert.JsonAsserter.isEqualTo(JsonAsserter.java:101)
        at com.bwgjoseph.sccprovider.contracts.ContractVerifierTest.validate_getAllProfiles(ContractVerifierTest.java:39)
```

As `SCC` also verify the actual value, which will trigger the actual method call to get the value, it will most likely mismatch and fail the test. So we have to `mock` the value via `Mockito`, and the easier way is to create a separate service to call from controller, so that we can mock that service return value more easily. Unlike `pact` where the actual value is less important(? to verify)