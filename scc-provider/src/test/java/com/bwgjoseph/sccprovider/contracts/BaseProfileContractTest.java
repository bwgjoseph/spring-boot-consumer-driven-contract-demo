package com.bwgjoseph.sccprovider.contracts;

import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bwgjoseph.sccprovider.Profile;
import com.bwgjoseph.sccprovider.ProfileController;
import com.bwgjoseph.sccprovider.ProfileService;
import com.bwgjoseph.sccprovider.SccProviderApplication;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

/**
 * This is the base class for all Profile related API
 */
// 1. Define the base contract class, can be everything in one, or per API type one
// which we need to declare in `build.gradle` to indicate the `baseClassForTests`
@SpringBootTest(classes = SccProviderApplication.class)
public abstract class BaseProfileContractTest {
    @Autowired
    private ProfileController profileController;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProfileService profileService;

    @BeforeEach
    public void beforeEach() {
        List<Profile> profiles = List.of(
            Profile.builder().id(1).name("Joseph").age(22).email("jose@gmail.com").dob(LocalDate.of(2000, 1, 1)).build(),
            Profile.builder().id(2).name("Sam").age(32).email("sam@gmail.com").dob(LocalDate.of(2000, 1, 1)).build()
        );

        // 2. Given that this test is set to run, we configure to use `RestAssuredMockMvc`
        // with the given setup
        RestAssuredMockMvc.standaloneSetup(
                MockMvcBuilders
                    .standaloneSetup(profileController)
                    // set to use existing objectMapper because of jackson date mapping
                    .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                );

        /**
         * Providing mock data of what would be returned when the respective methods are called
         * This is used when `contractTest` task is triggered
         */
        // 3. We don't want to trigger our actual implementation
        // so we mock the return data
        // The mocks will be used when running `contractTest` task
        when(profileService.getAllProfiles()).thenReturn(profiles);
        when(profileService.getProfile(1)).thenReturn(Optional.of(profiles.get(0)));
    }
}
