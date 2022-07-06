# Pact Message Consumer

Pact supports testing of [asynchronous messages](https://docs.pact.io/implementation_guides/jvm/consumer/junit5#asynchronous-messages) which are typically associated from message broker such as `RabbitMQ, and Kafka`. This allows us to test not only HTTP contract but also messaging contract

## Configuration

- In `ProfileListenerTests`, add `@ExtendWith(PactConsumerTestExt.class)` at class level
  - For `JUnit 5` which replaces `PactRunner` in `JUnit 4`
- Add `@PactTestFor` at class level
  - Define `providerName`
    - When `Provider` run the test, it will specify `@Provider("ProfileProvider")` to match against `providerName`
  - Define `providerType`
    - as `ProviderType.ASYNCH`, this is because we are testing for messaging
- Define the different interaction expectation
  - For each different message interaction, define a `@Pact` method to represent the expected response
  - At minimal, define the `consumer` parameter
    - This will result in generating the expectation into a single contract per provider
    - Contracts are generated based on unique `[Consumer]-[Provider].json` naming
- For each `@Test`
  - Specify `@PactTestFor(pactMethod = "profileMessage")` where `pactMethod` refers to the method annotated with `@Pact`
  - This is so that it knows which method to retrieve the expected response from
  - Note that, unlike HTTP test where we inject `MockServer` as the parameter argument, for messaging test, we inject `List<Message> messages`

## Generate Contract

Once the test are written, to generate the contract, run

```
./gradlew test
```

or

```
./gradlew build
```

Once completed, it will output the generated consumer contract to `/pact-message-consumer/build/pacts/`

## Publishing to Broker

- Add `id 'au.com.dius.pact' version '4.4.0-beta.2'` to `build.gradle > plugins`
- Add `pack.broker` and `pack.publish` information - see `build.gradle`
  - Note that some properties can be configured as [JVM System Properties](https://github.com/pact-foundation/pact-jvm/blob/master/provider/gradle/README.md#configured-as-jvm-system-properties) or [Environment Variables](https://github.com/pact-foundation/pact-jvm/blob/master/provider/gradle/README.md#configured-as-environment-variables)
- Run `./gradlew build pactPublish`

Once published, we can see that the `Contract` is listed in the `Broker`

![pact-broker-1](/assets/pact-broker-6.png)

It seem that the messaging contract is not parsable by the `broker`

![pact-broker-2](/assets/pact-broker-7.png)


## References

- https://github.com/SrinivasanTarget/ContractTestingBoilerplate
- https://github.com/pact-foundation/pact-jvm/blob/master/consumer/junit5/src/test/java/au/com/dius/pact/consumer/junit5/AsyncMessageTest.java
- https://dius.com.au/2017/08/22/contract-testing-serverless-and-asynchronous-applications/
- https://docs.pact.io/recipes/kafka#schema-registry-json-consumer