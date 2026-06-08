package com.realtimeportfolio.marketdata;

import com.realtimeportfolio.common.config.kafka.KafkaProducerConfig;
import com.realtimeportfolio.common.config.kafka.KafkaTopicConfig;
import com.realtimeportfolio.common.config.redis.RedisConfig;
import com.realtimeportfolio.common.exception.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

//@EnableScheduling
@Import({
        GlobalExceptionHandler.class,
        KafkaProducerConfig.class,
        KafkaTopicConfig.class,
        RedisConfig.class
})
@SpringBootApplication(scanBasePackages = {"com.realtimeportfolio.common.exception","com.realtimeportfolio.marketdata"})
public class StackMarketDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(StackMarketDataApplication.class, args);
    }
}
