package com.bwgjoseph.pactmessageconsumer;

import static org.assertj.core.api.Assertions.entry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.messaging.Message;
import au.com.dius.pact.core.model.messaging.MessagePact;

/**
 * @see <a href="https://github.com/pact-foundation/pact-jvm/blob/master/consumer/junit5/src/test/java/au/com/dius/pact/consumer/junit5/AsyncMessageTest.java">AsyncMessageTest</a>
 */
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "ProfileMessageProvider", providerType = ProviderType.ASYNCH, pactVersion = PactSpecVersion.V3)
public class ProfileListenerTests {

    @Pact(consumer = "ProfileMessageConsumer")
    MessagePact profileMessage(MessagePactBuilder builder) {

        PactDslJsonBody body = new PactDslJsonBody()
                        .integerType("id", 1)
                        .stringType("name", "fake")
                        .integerType("age", 10)
                        .stringType("email", "fake@gmail.com")
                        .dateExpression("dob", "^\\d{4}-\\d{2}-\\d{2}$", "yyyy-MM-dd")
                        .asBody();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("version", 1);

        return builder
            // in messaging test, it seem that state is not quite necessary?
            .given("profile")
            .expectsToReceive("valid profile message from message broker")
            .withMetadata(metadata)
            .withContent(body)
            .toPact();
    }

    // Sample output
    // [
    // Message(
    //     description='valid profile message from message broker',
    //     providerStates=[ProviderState(name=profile, params={})],
    //     contents=PRESENT({"age":10,"dob":"2000-01-31","email":"fake@gmail.com","id":1,"name":"fake"}),
    //     matchingRules=MatchingRules(rules={body=MatchingRuleCategory(name=body, matchingRules={$.id=MatchingRuleGroup(rules=[NumberTypeMatcher(numberType=INTEGER)], ruleLogic=AND, cascaded=false), $.name=MatchingRuleGroup(rules=[au.com.dius.pact.core.model.matchingrules.TypeMatcher@6bf08014], ruleLogic=AND, cascaded=false), $.age=MatchingRuleGroup(rules=[NumberTypeMatcher(numberType=INTEGER)], ruleLogic=AND, cascaded=false), $.email=MatchingRuleGroup(rules=[au.com.dius.pact.core.model.matchingrules.TypeMatcher@6bf08014], ruleLogic=AND, cascaded=false), $.dob=MatchingRuleGroup(rules=[DateMatcher(format=yyyy-MM-dd)], ruleLogic=AND, cascaded=false)})}),
    //     generators=Generators(categories={BODY={$.dob=DateGenerator(format=yyyy-MM-dd, expression=^\d{4}-\d{2}-\d{2}$)}}),
    //     metadata={version=1, contentType=application/json})
    // ]
    // Which is the same as the one generated in /build/pacts/*.json
    @Test
    @PactTestFor(pactMethod = "profileMessage")
    void testProfileMessage(List<Message> messages) throws JsonMappingException, JsonProcessingException {
        Assertions.assertThat(messages).isNotEmpty();

        // add `jackson-datatype-jsr310` to handle java8 datatime
        ObjectMapper objectMapper = new ObjectMapper();
        // since we are not using spring injected ObjectMapper, we need to register the module ourselves
        objectMapper.findAndRegisterModules();

        Profile profile = objectMapper.readValue(new String(messages.get(0).contentsAsBytes()), Profile.class);

        Assertions.assertThat(profile)
            .hasFieldOrPropertyWithValue("id", 1)
            .hasFieldOrPropertyWithValue("name", "fake")
            .hasFieldOrPropertyWithValue("age", 10)
            .hasFieldOrPropertyWithValue("email", "fake@gmail.com")
            .hasFieldOrProperty("dob");

        Map<String, Object> metadata = messages.get(0).getMetadata();

        Assertions.assertThat(metadata)
            .contains(
                entry("version", 1),
                entry("contentType", "application/json")
                );
    }
}
