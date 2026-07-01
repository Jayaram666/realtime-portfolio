package com.realtimeportfolio.authentication.producer;

import com.realtimeportfolio.common.config.kafka.KafkaTopics;
import com.realtimeportfolio.common.dto.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCreatedProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void produce(UserCreatedEvent userCreatedEvent) {
        log.info("Publishing user created event");
        kafkaTemplate.send(KafkaTopics.USER_CREATED, userCreatedEvent);
        log.info("published user created event");
    }
}
