package com.bwgjoseph.sccmessageconsumer;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.StubTrigger;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {"stubrunner.amqp.enabled=true"})
@AutoConfigureStubRunner(ids = "com.bwgjoseph:scc-message-provider:+:stubs:8100", stubsMode = StubsMode.LOCAL)
public class ProfileMessageConsumerTests {

    @Autowired
    private StubTrigger stubTrigger;

    @Autowired
    private ProfileListener profileListener;

    @Test
    void shouldListenToProfileMessage() {
        Profile expectedProfile = Profile.builder().id(1).name("Joseph").age(22).email("jose@gmail.com").dob(LocalDate.of(2001, 1, 1)).build();

        this.stubTrigger.trigger("profile.created.event");

        Assertions.assertThat(this.profileListener.getProfiles()).hasSize(1);
        Assertions.assertThat(this.profileListener.getProfiles().get(0)).usingRecursiveComparison().isEqualTo(expectedProfile);
    }
}
