package com.realtimeportfolio.portfolio.consumer;


import com.realtimeportfolio.common.config.kafka.KafkaConsumerGroups;
import com.realtimeportfolio.common.config.kafka.KafkaTopics;
import com.realtimeportfolio.common.dto.UserCreatedEvent;
import com.realtimeportfolio.portfolio.handler.UserCreatedHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserConsumer {

    private final UserCreatedHandler userCreatedHandler;

    @KafkaListener(
            topics = KafkaTopics.USER_CREATED,
            groupId = KafkaConsumerGroups.PORTFOLIO_SERVICE_GROUP
    )
    public void consume(UserCreatedEvent userCreatedEvent) {
        log.info("Received user created event ");
        userCreatedHandler.handleUserCreated(userCreatedEvent);
        log.info("Created user and assigned the menu permissions");
    }
}
