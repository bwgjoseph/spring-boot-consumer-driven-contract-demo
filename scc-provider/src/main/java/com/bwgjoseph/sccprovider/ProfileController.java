package com.bwgjoseph.sccprovider;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/profiles")
    public List<Profile> getAllProfiles() {
        return this.profileService.getAllProfiles();
    }

    @GetMapping("/profiles/{id}")
    public Optional<Profile> getProfile(@PathVariable("id") int id) {
        return this.profileService.getProfile(id);
    }

}
