package com.portfolio;

import com.portfolio.kafka.KafkaConsumerConfig;
import com.portfolio.kafka.KafkaTopicConfig;
import com.portfolio.rabbitmq.RabbitMQConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;


@SpringBootApplication(scanBasePackages = {
        "com.portfolio",
        "org.portfolio"
})
@Import({
        RabbitMQConfig.class
})
public class NotificationApplication {
}
