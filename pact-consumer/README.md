# Pact Consumer

## Configuration

- In `ProfileClientTests`, add `@ExtendWith(PactConsumerTestExt.class)` at class level
  - For `JUnit 5` which replaces `PactRunner` in `JUnit 4`
- Add `@PactTestFor` at class level
  - This starts a `MockServer` for the consumer to run the test against which is why we have to set this `this.profileClient.setBaseUrl(mockServer.getUrl());` within the test to replace the actual URL with the `MockServer` one
  - Define `providerName`
    - When `Provider` run the test, it will specify `@Provider("ProfileProvider")` to match against `providerName`
- Define the different interaction expectation
  - For each different API interaction, define a `@Pact` method to represent the expected response
  - At minimal, define the `consumer` parameter
    - This will result in generating the expectation into a single contract per provider
    - Contracts are generated based on unique `[Consumer]-[Provider].json` naming
- For each `@Test`
  - Specify `@PactTestFor(pactMethod = "getAllProfiles")` where `pactMethod` refers to the method annotated with `@Pact`
  - This is so that it knows which method to retrieve the expected response from

Need further understanding on what it means for [matching the interactions by provider name](https://docs.pact.io/implementation_guides/jvm/consumer/junit5#matching-the-interactions-by-provider-name) and [matching the interactions by method name](https://docs.pact.io/implementation_guides/jvm/consumer/junit5#matching-the-interactions-by-method-name)

## Generate Contract

Once the test are written, to generate the contract, run

```
./gradlew test
```

or

```
./gradlew build
```

Once completed, it will output the generated consumer contract to `/pact-consumer/build/pacts/` which can then be copied to provider project for verification or publish to a `Pact Broker`

As we are not using `Pact Broker/Server` at this stage, we have to manually copy the contract that was generated by the `Consumer` and place it on `/pact-provider/pacts` directory

## Publishing to Broker

- Add `id 'au.com.dius.pact' version '4.4.0-beta.2'` to `build.gradle > plugins`
- Add `pack.broker` and `pack.publish` information - see `build.gradle`
  - Note that some properties can be configured as [JVM System Properties](https://github.com/pact-foundation/pact-jvm/blob/master/provider/gradle/README.md#configured-as-jvm-system-properties) or [Environment Variables](https://github.com/pact-foundation/pact-jvm/blob/master/provider/gradle/README.md#configured-as-environment-variables)
- Run `./gradlew build pactPublish`

Once published, we can see that the `Contract` is listed in the `Broker`

![pact-broker-1](/assets/pact-broker-1.png)

Interestingly, we can also review the `contract` written by the `Consumer`

![pact-broker-2](/assets/pact-broker-2.jpg)

This allows us to manually verify the contract is written correctly as well

## Code Snippet

### PactDslJsonBody

```java
// {
//     "profiles": [
//         {
//             "id": 1,
//             "name": "Joseph",
//             "age": 22,
//             "email": "jose@gmail.com",
//             "dob": "2000-01-01"
//         },
//     ]
// }
new PactDslJsonBody()
    .minArrayLike("profiles", 1, 1)
        .integerType("id", 1)
        .stringType("name", "fake")
        .integerType("age", 10)
        .stringType("email", "fake@gmail.com")
        .date("dob")
        .closeObject()
    .closeArray()
```

### PactDslJsonArray

```java
// [
//     {
//         "id": 1,
//         "name": "Joseph",
//         "age": 22,
//         "email": "jose@gmail.com",
//         "dob": "2000-01-01"
//     }
// ]
PactDslJsonArray.arrayEachLike()
    .integerType("id", 1)
    .stringType("name", "fake")
    .integerType("age", 10)
    .stringType("email", "fake@gmail.com")
    .date("dob")
.closeObject()
```