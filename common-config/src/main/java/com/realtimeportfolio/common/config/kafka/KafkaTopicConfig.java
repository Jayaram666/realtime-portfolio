package com.realtimeportfolio.common.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers")
public class KafkaTopicConfig {

    @Bean
    public NewTopic stockPriceUpdatesTopic() {
        return TopicBuilder.name(KafkaTopics.STOCK_PRICE_UPDATES)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic userCreatedTopic() {
        return TopicBuilder.name(KafkaTopics.USER_CREATED)
                .partitions(3)
                .replicas(1)
                .build();
    }
}