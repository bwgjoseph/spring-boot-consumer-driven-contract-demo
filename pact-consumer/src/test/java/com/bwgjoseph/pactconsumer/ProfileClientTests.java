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
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "ProfileProvider", pactVersion = PactSpecVersion.V3, port = "9999")
public class ProfileClientTests {
    @Autowired
    private ProfileClient profileClient;

    @Pact(consumer = "ProfileConsumer")
    public RequestResponsePact getAllProfiles(PactDslWithProvider builder) {
        return builder
            .given("profiles exists")
                .uponReceiving("get all profiles")
                .path("/profiles")
            .willRespondWith()
                .status(200)
                .body(
                    // https://docs.pact.io/implementation_guides/jvm/consumer#root-level-arrays-that-match-all-items
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

    @Test
    @PactTestFor(pactMethod = "getAllProfiles")
    void testGetAllProfiles(MockServer mockServer) {
        this.profileClient.setBaseUrl(mockServer.getUrl());

        List<Profile> profiles = this.profileClient.getAllProfiles();

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
