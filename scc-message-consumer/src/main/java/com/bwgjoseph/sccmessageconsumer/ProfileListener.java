package com.bwgjoseph.sccmessageconsumer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ProfileListener {
    private List<Profile> profiles = new ArrayList<>();

    @RabbitListener(queues = "profileq")
    public void listen(Profile profile) {
        this.profiles.add(profile);
    }

    public List<Profile> getProfiles() {
        return this.profiles;
    }
}
