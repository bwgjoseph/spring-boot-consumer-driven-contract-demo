package com.bwgjoseph.sccconsumer.contracts;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.bwgjoseph.sccconsumer.Profile;

@SpringBootTest
/**
 * Configure to fetch the stub from local .m2 repository, other option are CLASSPATH and REMOTE
 *
 * What this does is:
 *  - Download stub
 *  - Start Wiremock Server on port 8100
 */
@AutoConfigureStubRunner(ids = "com.bwgjoseph:scc-provider:+:stubs:8100", stubsMode = StubsMode.LOCAL)
class ProfileConsumerTests {

    @Test
    void testGetAllProfiles() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<Profile>> profiles = restTemplate.exchange("http://localhost:8100/profiles", HttpMethod.GET, null, new ParameterizedTypeReference<List<Profile>>(){});

        assertThat(profiles.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(profiles.getBody().size()).isEqualTo(2);
        assertThat(profiles.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        Profile profile1 = profiles.getBody().get(0);
        assertThat(profile1.getId()).isEqualTo(1);
        assertThat(profile1.getName()).isEqualTo("Joseph");
        assertThat(profile1.getAge()).isEqualTo(22);
        assertThat(profile1.getEmail()).isEqualTo("jose@gmail.com");

        Profile profile2 = profiles.getBody().get(1);
        assertThat(profile2.getId()).isEqualTo(2);
        assertThat(profile2.getName()).isEqualTo("Sam");
        assertThat(profile2.getAge()).isEqualTo(32);
        assertThat(profile2.getEmail()).isEqualTo("sam@gmail.com");
    }

    @Test
    void testGetProfilesById1() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Profile> profile = restTemplate.exchange("http://localhost:8100/profiles/1", HttpMethod.GET, null, Profile.class);

        assertThat(profile.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(profile.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        assertThat(profile.getBody().getId()).isEqualTo(1);
        assertThat(profile.getBody().getName()).isEqualTo("Joseph");
        assertThat(profile.getBody().getAge()).isEqualTo(22);
        assertThat(profile.getBody().getEmail()).isEqualTo("jose@gmail.com");
    }
}