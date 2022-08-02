# Q&A

## Pact

### Defining the payload

In [ProfileClientTests](/pact-consumer/src/test/java/com/bwgjoseph/pactconsumer/ProfileClientTests.java), we define the following `respondBody`

```java
.body(
    PactDslJsonArray.arrayEachLike()
        .integerType("id", 1)
        .stringType("name", "fake")
        .integerType("age", 10)
        .stringType("email", "fake@gmail.com")
        .date("dob")
    .closeObject()
)
```

To further clarify, if the value were to be define with specific `values` then the response body generated in [ProfileConsumer-ProfileProvider.json](/pact-consumer/build/pacts/ProfileConsumer-ProfileProvider.json) would be what was defined. And that would be the `response` returned back by the `MockServer` when running the test.

If we don't want to provide the value, it can be left empty (to let pact generate a random value for you) like such

```java
.body(
    PactDslJsonArray.arrayEachLike()
        .integerType("id", 1)
        .stringType("name") // <-- notice that "fake" has been removed
        .integerType("age", 10)
        .stringType("email", "fake@gmail.com")
        .date("dob")
    .closeObject()
)
```

The generated response in the `json` file will be like such

```json
// before
"body": [
    {
        "age": 10,
        "dob": "2000-01-31",
        "email": "fake@gmail.com",
        "id": 1,
        "name": "fake"
    }
],

// after
"body": [
    {
        "age": 10,
        "dob": "2000-01-31",
        "email": "fake@gmail.com",
        "id": 1,
        "name": "string"
    }
],
```

### What is @State

Read the following:

- [provider_states](https://docs.pact.io/getting_started/provider_states)
- [terminology#provider-state](https://docs.pact.io/getting_started/terminology#provider-state)

Should give you some idea and context to what `@State` is about. In summary, it is to allow provider to match the expectation of the expected state in order to replay (verify) the contract.

For example, in `consumer`, it state that `given that profile john exist with age of 35`, then the provider would have to provide the correct `state` before running the test to ensure that there will be a profile name john with age 35 available.

### Providing non-fixed value

See [dsl-matching-methods-doc](https://docs.pact.io/implementation_guides/jvm/consumer/junit#dsl-matching-methods)

> In most cases, they take an optional value parameter which will be used to generate example values (i.e. when returning a mock response). If no example value is given a random one will be generated.
