# Pact Provider

## Configuration

- In `PactProfileConsumerVerificationTest`, add `@Provider("ProfileProvider")` at class level
  - Note `ProfileProvider` is the same as defined in consumer `@PactTestFor(providerName)`
- Add `@PactFolder("pacts")` at class level
  - To indicate where to grab the `contract` from
  - If we are using broker, then this is not necessary and will be replaced by `@PactBroker` annotation
- Note that for Spring Provider, we have to bring in `au.com.dius.pact.provider:junit5spring` package instead of `au.com.dius.pact.provider:junit5`
- Add `@ExtendWith(PactVerificationSpringProvider.class)` and `@TestTemplate`
  - This setup to run against all the contracts defined by `Consumer`
- Add `@BeforeEach`
  - This setup the "test client" to run against the `Provider`
  - See [test-target docs](https://docs.pact.io/implementation_guides/jvm/provider/junit5#test-target)
  - For `Spring`, it supports additional [test targets](https://docs.pact.io/implementation_guides/jvm/provider/junit5spring#modifying-requests) like `MockMvcTestTarget` and `WebFluxTarget`
- Add `@State`
  - With `value` that should match against `Consumer.@Pact.given` state
  - `State` is to help to provide `data` specifically for the contract test, and not rely on the actual data (state) of the application (especially if it reads from the database)
  - The default `state.action` is `StateChangeAction.SETUP` so can ignore unless it's `TEARDOWN`
  - The body of `@State` is to mock the return of the actual value to be returned (so it work the same just like normal mocking would)
    - In this case, we add `@MockBean` to `ProfileController` and set the return value
    - In the event that there was some call to other component, then we will just mock that component return value

Not quite sure why the need to explicitly indicate each `@State` although I think it doesn't have to but can't find the docs for it yet

## Verifying Contract from Broker

- For `Provider` with `Spring`, we just need to provide some configuration and it will be able to read the `contracts` from the `Broker`
- It is not necessary to bring in `au.com.dius.pact` gradle plugin as we can rely on `JUnit Test` to [publish the result](https://github.com/pact-foundation/pact-jvm/issues/1567#issuecomment-1157275733) via `property`
  - `gradle plugin` in necessary if you wish to use `pactVerify` gradle command
- Navigate to `application.properties` and add
```
pactbroker.host: localhost
pactbroker.port: 9292
pactbroker.auth.username: pact
pactbroker.auth.password: pact
```
- Switch out `@PactFolder` with `@PactBroker`
  - It is also possible to configure host, auth, etc through `@PactBroker`

Before we verify the result, we can take a look at the `Matrix` page

![pack-broker-3](/assets/pact-broker-3.png)

- To verify and publish the result
  - ~~Add `pack.broker` and `pact.serviceProviders` in `build.gradle`~~
  - ~~Add `pact.verifier.publishResults=true` in `gradle.properties`~~
  - ~~Run `./gradlew build pactVerify`~~
  - Configure `test` task as such
    ```groovy
    tasks.named('test') {
      useJUnitPlatform()

      if (project.hasProperty("publishResults")) {
          systemProperty "pact.verifier.publishResults", project.publishResults
      }
    }
    ```
  - Run `./gradlew clean test -PpublishResults=true`

> The reason to set to `publishResults` is because for some reason either PowerShell or Gradle doesn't like `pact` as a property, and [stripped](https://github.com/pact-foundation/pact-jvm/issues/1567#issuecomment-1157269806) it off, hence, we pass in using `publishResults` which still get recognize as `pact.verifier.publishResults`

![pack-broker-5](/assets/pact-broker-5.jpg)

## Tips

- Consider adding `@Tags` to `Pact` test suite and then configure `test` task to exclude `@Tags("pact")`
  - This is so that the usual test won't run test tag with `pact`
  - See [this](https://stackoverflow.com/questions/64322037/how-to-publish-pact-verification-result-to-pact-broker-in-gradle)
