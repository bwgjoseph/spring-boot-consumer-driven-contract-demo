# Spring Boot Consumer Driven Contract Demo

This project is written to attempt to understand more about `Consumer Driven Contract` with the use of Spring Boot + [Pact](https://pact.io/) / [Spring Cloud Contract](https://spring.io/projects/spring-cloud-contract)

- [Spring Boot Consumer Driven Contract Demo](#spring-boot-consumer-driven-contract-demo)
  - [Goal](#goal)
  - [Project](#project)
    - [Run](#run)
    - [Build](#build)
  - [Pact CDC](#pact-cdc)
    - [Broker](#broker)
    - [Known Issue](#known-issue)
  - [Spring Cloud Contract CDC](#spring-cloud-contract-cdc)
    - [Known Issue](#known-issue-1)
  - [Consideration](#consideration)
    - [Developer Experience](#developer-experience)
      - [Documentation](#documentation)
      - [Tooling Support](#tooling-support)
    - [Features](#features)
  - [Further exploration](#further-exploration)
  - [Sample](#sample)
  - [Reference](#reference)

## Goal

- [x] Basic understanding and setup of Pact consumer / provider
- [x] Basic understanding and setup of Pact server
- [x] Basic understanding and setup of SCC provider / consumer
- [x] HTTP contract test for Pact
- [x] Messaging contract test for Pact
- [x] HTTP contract test for SCC
- [x] Messaging contract test for SCC
- [ ] Using SCC with Pact Broker

## Project

This repository includes four distinct sub-project;

- [pact-consumer](./pact-consumer/)
- [pact-provider](./pact-provider/)
  - Provides two API
    - GET /profiles
    - GET /profiles/{id}

- [scc-provider](./scc-provider/)
- [scc-consumer](./scc-consumer/)

### Run

Navigate to the individual directory and run

```bash
./gradlew bootRun
```

### Build

Navigate to the individual directory and run

```bash
./gradlew [clean] build
```

## Pact CDC

### Broker

As mentioned earlier, without using `Broker`, we need to transfer the generated contract from the `Consumer` to `Provider` manually. However, with `Broker`, we can simply ask the `Consumer` to publish the `contracts` to `Broker`, and the `Provider` will read and verify the `contracts` from the `Broker`. This allows for even more seamless integration test and the `Broker` also has UI to view the result

To startup the broker, navigate to root directory and run

```
docker-compose up -d
```

Access via http://localhost:9292

### Known Issue

- Since `au.com.dius.pact.consumer:junit5:4.3.0` onwards, when running the test, it will throw error `java.lang.UnsupportedOperationException: Method getSingleProfile does not conform required method signature 'public au.com.dius.pact.core.model.V4Pact xxx(PactBuilder builder)'`
  - See [test-report](issues/pact-4.3.0/test/index.html), most likely related to this [issue](https://github.com/pact-foundation/pact-jvm/issues/1488) and this [note](https://docs.pact.io/implementation_guides/jvm/upgrade-to-4.3.x)
  - To overcome this, we can change to `PactSpecVersion.V3` like such `@PactTestFor(providerName = "ProfileProvider", pactVersion = PactSpecVersion.V3)`
  - Seem like `PactSpecVersion.V4` is the default, and is incompatible with `V3`
- When running the test via `VSCode` (manual click), `pact` generated contract will be output to `target/pact` even though using `gradle`. However, if running via command `./gradlew build`, there won't be such issue
- Unable to publish verification result to broker after running `./gradlew pactVerify` command. Have reported the [issue](https://github.com/pact-foundation/pact-jvm/issues/1567).
  - Managed to get the result published after some help but still facing issue when using with `gradle plugin`



## Spring Cloud Contract CDC

### Known Issue

- `start.spring.io` [wrongly generated](https://github.com/spring-cloud/spring-cloud-contract/issues/1795) `contracts` as `task` instead of `extension` for `gradle`

## Consideration

- Consumer Driven Contract [Pact] vs Provider Driven Contract [SCC]
- Ease of use
- Learning curve / Overhead

### Developer Experience

#### Documentation

- Both libraries don't provide the best out-of-the-box experience to start as the documentation seem to jump all around, and are quite confusing (sometimes)

#### Tooling Support

- So far, I am using `gradle` to test in my demo, and online is lacking on the support for `gradle`. Most of the example are written using `maven`, and my attempt to use `gradle` has faced multiple issues.
  - Especially when an error occurs, it wasn't quite clear of what exactly is happening. I have to enable `--info or --debug` to see what exactly is the issue
  - One example was when using `SCC`, in my contract, I did not enclose the `response.body.email` with quotes, but the error was pointing at `Line 5` of the `getAllProfiles.groovy` file which was the start of the contract and was able to know when I turn on `--debug` mode
- To be fair, if I had chose to use `maven`, the experience should have been better

### Features

- In `Pact`, `consumer` writes what fields are required in the `contract` before submitting, and `provider` run against it
- In `SCC`, `provider` provides the `stub` and `consumer` run against the `stub`, then verify if the fields they need are there
- In `SCC`, it seem less straightforward to write contract against `Date`

## Further exploration

Possibly look into using [Spring Cloud Contract](https://spring.io/projects/spring-cloud-contract#overview) with [pact-broker](https://cloud.spring.io/spring-cloud-contract/reference/html/howto.html#how-to-use-pact-broker)
Look into [scc-multiapi-converter](https://github.com/corunet/scc-multiapi-converter)

## Sample

- [spring-cloud-contract-sample](https://github.com/maliksalman/spring-cloud-contract-sample)

## Reference

- [pact-workshop-Maven-Springboot-JUnit5](https://github.com/pact-foundation/pact-workshop-Maven-Springboot-JUnit5)
- [contract-test-spring-cloud-contract-vs-pact](https://blog.devgenius.io/contract-test-spring-cloud-contract-vs-pact-420450f20429)
- [pact-jvm-example](https://arxman.com/pact-jvm-example/)
- [consumer-driven-contract-tests-lessons-learned](https://medium.com/kreuzwerker-gmbh/consumer-driven-contract-tests-lessons-learned-b4e1ac471d0c)
- [okta-spring-cloud-contract](https://developer.okta.com/blog/2022/02/01/spring-cloud-contract)
- [scc-contract-dsl-http-top-level-elements](https://docs.spring.io/spring-cloud-contract/docs/current/reference/html/project-features.html#contract-dsl-http-top-level-elements)
- [gradle-springboot-mavenpublish-publication-only-contains-dependencies](https://stackoverflow.com/questions/61500897/gradle-springboot-mavenpublish-publication-only-contains-dependencies-and-or)
- [continuous-integration-with-jenkins-artifactory-and-spring-cloud-contract](https://piotrminkowski.com/2018/07/04/continuous-integration-with-jenkins-artifactory-and-spring-cloud-contract/)
- [spring-cloud-contract](https://www.baeldung.com/spring-cloud-contract)
- [pact-vs-spring-cloud-contract-tests](https://stackoverflow.com/questions/52033686/pact-vs-spring-cloud-contract-tests)
- [introduction-to-consumer-driven-contract-testing](https://medium.com/kreuzwerker-gmbh/introduction-to-consumer-driven-contract-testing-3a130c8c2ea0)
- [way-to-microservices-contract-testing-a-spring/pact-implemantation](https://www.kloia.com/blog/way-to-microservices-contract-testing-a-spring/pact-implemantation)
- [consumer-driven-contract-testing](https://inspeerity.com/blog/consumer-driven-contract-testing)
- [howto-consumer-driven-contracts-with-spring-cloud-contract](https://rieckpil.de/howto-consumer-driven-contracts-with-spring-cloud-contract/)
- [pact-jvm-demo](https://github.com/ythirion/pact-jvm-demo)
- [pact examples](https://docs.pactflow.io/docs/examples/)
- [event-driven-architecture-how-to-perform-contract-testing-in-kafka-pubsub](https://blog.testproject.io/2020/06/03/event-driven-architecture-how-to-perform-contract-testing-in-kafka-pubsub/)
- [ContractTestingBoilerplate](https://github.com/SrinivasanTarget/ContractTestingBoilerplate)
- [how-to-test-java-microservices-with-pact](https://blogs.oracle.com/javamagazine/post/how-to-test-java-microservices-with-pact)
- [consumer-driven-contract-testing-with-pact](https://blog.risingstack.com/consumer-driven-contract-testing-with-pact/)