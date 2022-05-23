package com.bwgjoseph.pactprovider;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfileController {
    private final List<Profile> profiles = List.of(
        Profile.builder().id(1).name("Joseph").age(22).email("jose@gmail.com").dob(LocalDate.of(2000, 1, 1)).build(),
        Profile.builder().id(2).name("Bryan").age(37).email("bryan@gmail.com").dob(LocalDate.of(1985, 1, 1)).build(),
        Profile.builder().id(3).name("Illie").age(54).email("illie@gmail.com").dob(LocalDate.of(1968, 1, 1)).build()
    );

    @GetMapping("/profiles")
    public List<Profile> getAllProfiles() {
        return this.profiles;
    }

    @GetMapping("/profiles/{id}")
    public Optional<Profile> getProfile(@PathVariable("id") int id) {
        return this.profiles.stream().filter(profile -> profile.getId() == id).findFirst();
    }

}
