package com.bwgjoseph.sccmessageprovider;

import java.time.LocalDate;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileProducer {

    private final RabbitTemplate rabbitTemplate;

    // @EventListener(ApplicationReadyEvent.class)
    // public void listen() {
    //     this.publishProfileEvent();
    // }

    public void publishProfileEvent() {
        Profile profile = Profile.builder().id(1).name("Joseph").age(22).email("jose@gmail.com").dob(LocalDate.of(2001, 1, 1)).build();
        this.rabbitTemplate.convertAndSend("profile.exchange", "profilerk", profile);
    }
}
