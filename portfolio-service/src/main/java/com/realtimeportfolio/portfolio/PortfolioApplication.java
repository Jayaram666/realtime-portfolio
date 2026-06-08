package com.realtimeportfolio.portfolio;

import com.realtimeportfolio.common.config.kafka.KafkaConsumerConfig;
import com.realtimeportfolio.common.config.kafka.KafkaTopicConfig;
import com.realtimeportfolio.common.config.rabbitmq.RabbitMQConfig;
import com.realtimeportfolio.common.exception.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableKafka
//@EnableScheduling
@EnableFeignClients(basePackages = "com.realtimeportfolio.portfolio.client")
@Import({
        GlobalExceptionHandler.class,
        KafkaConsumerConfig.class,
        KafkaTopicConfig.class,
        RabbitMQConfig.class
})
@SpringBootApplication(scanBasePackages = "com.realtimeportfolio.portfolio")
public class PortfolioApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortfolioApplication.class, args);
    }
}
