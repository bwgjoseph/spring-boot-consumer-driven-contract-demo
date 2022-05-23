package com.bwgjoseph.pactconsumer;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileClient {

    private String baseUrl = "http://localhost:9090/";
    private final RestTemplate restTemplate;

    public List<Profile> getAllProfiles() {
        return this.restTemplate.exchange(baseUrl + "/profiles", HttpMethod.GET, null, new ParameterizedTypeReference<List<Profile>>() {}).getBody();
    }

    public Profile getSingleProfile(int id) {
        return this.restTemplate.exchange(baseUrl + "/profiles/{id}", HttpMethod.GET, null, Profile.class, id).getBody();
    }

    public void setBaseUrl(String url) {
        this.baseUrl = url;
    }

}
