package com.realtimeportfolio.authentication;

import com.realtimeportfolio.common.config.kafka.KafkaProducerConfig;
import com.realtimeportfolio.common.config.kafka.KafkaTopicConfig;
import com.realtimeportfolio.common.exception.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import({GlobalExceptionHandler.class,
        KafkaProducerConfig.class,
        KafkaTopicConfig.class})
@SpringBootApplication(scanBasePackages = {"com.realtimeportfolio.authentication","com.realtimeportfolio.common"})
public class AuthenticationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthenticationApplication.class, args);
    }
}
