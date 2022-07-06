# Pact Message Provider

## Configuration

- In `PactMessageProfileConsumerVerificationTests`, add `@Provider("ProfileMessageProvider")` at class level
  - Note `ProfileMessageProvider` is the same as defined in consumer `@PactTestFor(providerName)`
- Add `@PactBroker` at class level
  - Since this test does not require `Spring` annotation, specifying `@PactBroker` alone is not sufficient as it does not read from `application.properties`
  - So we have to define the properties directly into the annotation
  ```
  // like such
  @PactBroker(url = "http://localhost:9292", authentication = @PactBrokerAuth(username = "pact", password = "pact"))

  // and not split like this
  @PactBroker(url = "http://localhost:9292")
  @PactBrokerAuth(username = "pact", password = "pact")

  // or not just
  @PactBroker
  ```
  - Also have to define the protocol (http) explicitly
- Note that for Spring Provider, we have to bring in `au.com.dius.pact.provider:junit5spring` package instead of `au.com.dius.pact.provider:junit5`
- Add `@ExtendWith(PactVerificationSpringProvider.class)` and `@TestTemplate`
  - This setup to run against all the contracts defined by `Consumer`
- Add `@BeforeEach`
  - This setup the "test client" to run against the `Provider`
  - See [test-target docs](https://docs.pact.io/implementation_guides/jvm/provider/junit5#test-target)
  - We have to setup the target as [MessageTestTarget](https://docs.pact.io/implementation_guides/jvm/provider/junit5#messagetesttarget)
- Add `@PactVerifyProvider` with description matches `expectsToReceive` from `consumer`
  - build and returns the object where provider will publish to the message broker
- Add `@State` but do nothing
  - This is because we declare `providerState` in consumer
  - Not sure if that is actually necessary for messaging test
- Configure `@JsonFormat` for `LocalDate` in `Profile`
  - `@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")`
  - This is so that during de-serialization process, it will map to the `pattern` specified
  - Otherwise, the test will fail due to mis-match
    - `body: $.dob Expected [2022,7,6] to match a datetime of 'yyyy-MM-dd': Unable to parse the date: [2022,7,6]`
  - There are other ways to overcome this, using `@JsonFormat` is just one of them

Not quite sure why the need to explicitly indicate each `@State` although I think it doesn't have to but can't find the docs for it yet

## Verifying Contract from Broker

- To verify and publish the result
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

![pack-broker-8](/assets/pact-broker-8.jpg)

## Issue

It seem that the `metadata` expected by `consumer` is not being asserted / verified in the test. To verify if there are additional configuration required, or is it a bug.