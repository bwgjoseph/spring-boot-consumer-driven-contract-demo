package com.bwgjoseph.pactprovider;

import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.spring.junit5.PactVerificationSpringProvider;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// 1. Indicate the Provider name; match against the one defined in the Consumer
@Provider("ProfileProvider")
// 2. Indicate the contract is to be provided from `PactBroker`
// See `application.properties`
// @PactFolder("pacts")
@PactBroker
public class PactProfileConsumerVerificationTest {
    @MockBean
    ProfileController profileController;

    @LocalServerPort
    private int port;

    // 3. Since we are using `@SpringBootTest` with random port setup,
    // we need to tell Pact the port number to run against
    @BeforeEach
    void setup(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", port));
    }

    // 4. This is to tell Pact to verify all contract defined by the Consumer
    // What this does is to generate a test case for each interaction found in the Contract
    @TestTemplate
    // 5. This provides better integration with Spring, such as
    // - define your properties in `application.properties`
    // - Run test against `@MockMvc` for lightweight test
    // See https://docs.pact.io/implementation_guides/jvm/provider/junit5spring#modifying-requests
    @ExtendWith(PactVerificationSpringProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    /**
     * Ensure the test will use the mock data rather than actual data
     */
    // 6. State means setting up of data which the Consumer expects during the test interaction
    // `State` is to help to provide `data` specifically for the contract test, and not rely on the actual data (state)
    // of the application (especially if it reads from the database)
    @State(value = "profiles 1 exists")
    void profilesWithId1() {
        when(profileController.getProfile(1))
            .thenReturn(Optional.of(Profile.builder().id(1).name("Joseph").age(22).email("jose@gmail.com").dob(LocalDate.of(2000, 1, 1)).build()));
    }

    // 7. To come back to this
    // Not sure why if we don't state anything, the test will pass
    // or even if we return something wrong, the test also pass
    // the verification should be an array of profiles with specific fields (see consumer)
    // but even if I return with no age in provider, the test pass as well
    // is it a bug? or is it that the consumer contract wasn't wrote correctly
    @State(value = "profiles exists")
    void profiles() {
        // Profile p1 = Profile.builder().id(1).name("Joseph").email("jose@gmail.com").dob(LocalDate.of(2000, 1, 1)).build();
        // // Profile p2 = Profile.builder().id(2).name("Joseph").age(22).email("jose@gmail.com").dob(LocalDate.of(2000, 1, 1)).build();
        // when(profileController.getAllProfiles())
        //     .thenReturn(List.of(p1));
    }
}
