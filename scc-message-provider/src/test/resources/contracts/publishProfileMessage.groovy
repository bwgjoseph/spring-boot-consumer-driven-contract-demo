package contracts;

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should publish profile message when profile is created"

    label "profile.created.event"

    input {
        triggeredBy("publishProfileEvent()")
    }

    outputMessage {
        sentTo "profile.exchange"

        body(file("profileMessageBody.json"))

        headers {
            messagingContentType applicationJson()
        }
    }
}