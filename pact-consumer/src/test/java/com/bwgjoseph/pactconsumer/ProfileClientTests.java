package com.bwgjoseph.pactconsumer;

import java.time.LocalDate;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;

@SpringBootTest
// 1. Provided by the library - au.com.dius.pact.consumer:junit5
@ExtendWith(PactConsumerTestExt.class)
// 2. Indicate the provider to run against, and other configuration
// Reason to define the `port` is because underlying, it starts a `MockServer` for `RestTemplate` to run against
@PactTestFor(providerName = "ProfileProvider", pactVersion = PactSpecVersion.V3, port = "9999")
public class ProfileClientTests {
    @Autowired
    private ProfileClient profileClient;

    // 3. This is the place where we define the contract
    @Pact(consumer = "ProfileConsumer")
    public RequestResponsePact getAllProfiles(PactDslWithProvider builder) {
        new PactDslJsonArray();
        return builder
            .given("profiles exists")
                .uponReceiving("get all profiles")
                .path("/profiles")
            // 4. Define the expected response
            .willRespondWith()
                .status(200)
                .body(
                    // https://docs.pact.io/implementation_guides/jvm/consumer#root-level-arrays-that-match-all-items
                    // 5. We don't have to define all fields provided by Provider, only those we requires
                    PactDslJsonArray.arrayEachLike()
                        .integerType("id", 1)
                        .stringType("name", "fake")
                        .integerType("age", 10)
                        .stringType("email", "fake@gmail.com")
                        .date("dob")
                    .closeObject()
                )
            .toPact();
    }

    // 6. Then we run the test against the `MockServer` (injected) setup by `Pact`
    // note: the pactMethod refers to the `methodName` above
    @Test
    @PactTestFor(pactMethod = "getAllProfiles")
    void testGetAllProfiles(MockServer mockServer) {
        this.profileClient.setBaseUrl(mockServer.getUrl());

        // 7. Call the API as usual
        List<Profile> profiles = this.profileClient.getAllProfiles();

        // 8. Assert what you want to
        Assertions.assertThat(profiles.size()).isEqualTo(1);
    }

    @Pact(consumer = "ProfileConsumer")
    public RequestResponsePact getSingleProfile(PactDslWithProvider builder) {
        return builder
            .given("profiles 1 exists", "id", 1)
                .uponReceiving("get profile with id 1")
                .path("/profiles/1")
            .willRespondWith()
                .status(200)
                .body(
                    new PactDslJsonBody()
                        .integerType("id", 1)
                        .stringType("name", "fake")
                        .integerType("age", 10)
                        .stringType("email", "fake@gmail.com")
                        .date("dob")
                )
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "getSingleProfile")
    void testGetSingleProfile(MockServer mockServer) {
        this.profileClient.setBaseUrl(mockServer.getUrl());

        Profile profile = this.profileClient.getSingleProfile(1);

        Assertions.assertThat(profile.getId()).isEqualTo(1);
        Assertions.assertThat(profile.getName()).isEqualToIgnoringCase("fake");
        Assertions.assertThat(profile.getAge()).isEqualTo(10);
        Assertions.assertThat(profile.getEmail()).isEqualToIgnoringCase("fake@gmail.com");
        Assertions.assertThat(profile.getDob()).isInstanceOf(LocalDate.class);
    }
}
