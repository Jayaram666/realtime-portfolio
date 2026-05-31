package com.portfolio;

import com.portfolio.kafka.KafkaProducerConfig;
import com.portfolio.kafka.KafkaTopicConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
        "com.portfolio",
        "org.portfolio"
})
@EnableScheduling
@Import({
        KafkaTopicConfig.class,
        KafkaProducerConfig.class
})
public class StackMarketDataApplication {

    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(StackMarketDataApplication.class, args);
    }
}
