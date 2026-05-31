package com.portfolio;

import com.portfolio.kafka.KafkaConsumerConfig;
import com.portfolio.kafka.KafkaTopicConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = {
        "com.portfolio",
        "org.portfolio"
})
@Import({
        KafkaTopicConfig.class,
        KafkaConsumerConfig.class
})
@EnableFeignClients(basePackages = "com.portfolio.client")
public class PortfolioApplication {

    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(PortfolioApplication.class, args);
    }
}