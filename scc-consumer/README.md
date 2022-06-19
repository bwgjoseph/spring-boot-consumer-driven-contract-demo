# SCC Consumer

Generate the project from [start.spring.io](https://start.spring.io/#!type=gradle-project&language=java&platformVersion=2.7.0&packaging=jar&jvmVersion=17&groupId=com.bwgjoseph&artifactId=scc-consumer&name=scc-consumer&description=Spring%20Boot%20SCC%20Consumer&packageName=com.bwgjoseph.scc-consumer&dependencies=devtools,lombok,configuration-processor,web,cloud-contract-stub-runner)

## Configuration

- Add `ProfileConsumerTests`
  - Add `@AutoConfigureStubRunner(ids = "com.bwgjoseph:scc-provider:+:stubs:8100", stubsMode = StubsMode.LOCAL)`
    - We configure the stub to run at `port 8100`
    - and `StubsMode` to `LOCAL` so it will pull from local maven repository, instead of `REMOTE`

That's actually all is required to run a the test against the `stub`
