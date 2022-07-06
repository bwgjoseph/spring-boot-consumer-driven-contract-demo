# SCC Message Consumer

## Configuration

- Add `ProfileMessageConsumerTests`
  - Add `@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {"stubrunner.amqp.enabled=true"})`
    - No need to start web server since not testing for HTTP
  - Add `@AutoConfigureStubRunner(ids = "com.bwgjoseph:scc-provider:+:stubs:8100", stubsMode = StubsMode.LOCAL)`
    - We configure the stub to run at `port 8100`
    - and `StubsMode` to `LOCAL` so it will pull from local maven repository, instead of `REMOTE`

That's actually all is required to run a the test against the `stub`
