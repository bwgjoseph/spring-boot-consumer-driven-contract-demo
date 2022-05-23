# Spring Boot Pact Demo

This project is written to attempt to understand more about `Consumer Driven Contract` with the use of Spring Boot + [Pact](https://pact.io/)

## Project

This repository includes two sub-project;

- [pact-consumer](./pact-consumer/)
- [pact-provider](./pact-provider/)

### Run

Navigate to the individual directory and run

```bash
./gradlew bootRun
```

## Consideration

- Consumer Driven Contract [Pact] vs Provider Driven Contract [SCC]
- Ease of use
- Learning curve / Overhead

## Further exploration

Possibly look into using [Spring Cloud Contract](https://spring.io/projects/spring-cloud-contract#overview) with [pact-broker](https://cloud.spring.io/spring-cloud-contract/reference/html/howto.html#how-to-use-pact-broker)

Reference:

- [pact-workshop-Maven-Springboot-JUnit5](https://github.com/pact-foundation/pact-workshop-Maven-Springboot-JUnit5)
- [contract-test-spring-cloud-contract-vs-pact](https://blog.devgenius.io/contract-test-spring-cloud-contract-vs-pact-420450f20429)