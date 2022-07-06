# SCC Message Provider

## Configuration

- Create the contract in `/test/resources/contracts/` which can be in `groovy, java, yaml, kotlin`
  - Choose `groovy` as it seem the most friendly out of all to write the contract in
- Create Base class in `/test/java/com/bwgjoseph/sccmessageprovider/contracts`.
  - The base class and contract is used to define the testing behavior
  - Add `@AutoConfigureMessageVerifier`
- Define the base class in `build.gradle` under `contracts` extension
  - `baseClassForTests = 'com.bwgjoseph.sccmessageprovider.contracts.BaseMessageProfileContractTests'`
- Run `./gradlew generateContractTests` to generate the contract
  - By default, it will be invoked before `check` task
  - This command takes the contract file (e.g `publishProfileMessage.groovy`) and generates `build/generated-test-sources/contractTest/java/com/bwgjoseph/sccmessageprovider/contracts/ContractVerifierTest.java` file where the assertion is based on the input from the contract file
- Add `stubrunner.amqp.enabled=true` to `@SpringBootTest` at `BaseMessageProfileContractTests` or `application.properties`
  - This config tell spring to [mock](https://developers.ascendcorp.com/contract-testing-for-event-driven-spring-cloud-contract-series-cc3e4f02f1ff) `RabbitTemplate`
- Run `./gradlew contractTest` to run the test against the contract we defined
  - In order for this to work correctly, we need to configure at least two bean
    - `MessageConverter` and `AmqpTemplate/RabbitTemplate`
      - `Queue, Exchange and Bindings` are optional in this case
      - See `RabbitMQConfiguration.java`
    - Reason is that when we define `stubrunner.amqp.enabled=true`, `ContractVerifierAmqpAutoConfiguration.java` will pick up, and spy `RabbitTemplate`
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

## References

- https://novotnyr.github.io/scrolls/enforcing-spring-cloud-contracts-over-amqp/
- https://github.com/novotnyr/spring-cloud-contract-amqp-demo
- https://developer.epages.com/blog/tech-stories/how-to-test-eventbased-services-using-contracts/
- https://github.com/spring-cloud/spring-cloud-contract/blob/main/spring-cloud-contract-verifier/src/main/java/org/springframework/cloud/contract/verifier/messaging/amqp/ContractVerifierAmqpAutoConfiguration.java