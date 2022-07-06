package com.bwgjoseph.sccmessageprovider.contracts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;

import com.bwgjoseph.sccmessageprovider.ProfileProducer;

@SpringBootTest(properties = {"stubrunner.amqp.enabled=true"})
@AutoConfigureMessageVerifier
public abstract class BaseMessageProfileContractTests {

    @Autowired
    private ProfileProducer profileProducer;

    // this is to match contract.triggeredBy
    void publishProfileEvent() {
        this.profileProducer.publishProfileEvent();
    }
}
