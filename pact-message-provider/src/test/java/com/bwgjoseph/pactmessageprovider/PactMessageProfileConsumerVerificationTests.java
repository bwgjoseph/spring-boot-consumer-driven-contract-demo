package com.bwgjoseph.pactmessageprovider;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit5.MessageTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerAuth;
import au.com.dius.pact.provider.spring.junit5.PactVerificationSpringProvider;

@PactBroker(url = "http://localhost:9292", authentication = @PactBrokerAuth(username = "pact", password = "pact"))
@Provider("ProfileMessageProvider")
public class PactMessageProfileConsumerVerificationTests {

    @TestTemplate
    @ExtendWith(PactVerificationSpringProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @BeforeEach
    void beforeEach(PactVerificationContext context) {
        context.setTarget(new MessageTestTarget());
    }

    // we have to specify @State because it was declared in the consumer side
    // but we don't actually have to mock anything for messaging test
    @State("profile")
    void profileState() {

    }

    // this is based from description from the contract which is based on
    // `expectsToReceive` when building the `MessagePactBuilder` in consumer
    @PactVerifyProvider("valid profile message from message broker")
    String verify() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        Profile profile = Profile.builder()
                            .id(2)
                            .name("fake")
                            .age(10)
                            .email("fake@gmail.com")
                            .dob(LocalDate.now())
                            .build();

        // return the expected message provider will publish
        // if consumer expected to have email but as provider, it was removed
        // this test would have then failed
        return objectMapper.writeValueAsString(profile);
    }
}
