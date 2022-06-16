package com.bwgjoseph.sccprovider.contracts;

import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.bwgjoseph.sccprovider.Profile;
import com.bwgjoseph.sccprovider.ProfileController;
import com.bwgjoseph.sccprovider.ProfileService;
import com.bwgjoseph.sccprovider.SccProviderApplication;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

/**
 * This is the base class for all Profile related API
 */
@SpringBootTest(classes = SccProviderApplication.class)
public abstract class BaseProfileContractTest {
    @Autowired
    private ProfileController profileController;

    @MockBean
    private ProfileService profileService;

    @BeforeEach
    public void beforeEach() {
        List<Profile> profiles = List.of(
            Profile.builder().id(1).name("Joseph").age(22).email("jose@gmail.com").dob(LocalDate.of(2000, 1, 1)).build(),
            Profile.builder().id(2).name("Sam").age(32).email("sam@gmail.com").dob(LocalDate.of(2000, 1, 1)).build()
        );

        RestAssuredMockMvc.standaloneSetup(profileController);

        when(profileService.getAllProfiles()).thenReturn(profiles);
        when(profileService.getProfile(1)).thenReturn(Optional.of(profiles.get(0)));
    }
}
