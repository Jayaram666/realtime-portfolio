package com.realtimeportfolio.notification;

import com.realtimeportfolio.common.config.rabbitmq.RabbitMQConfig;
import com.realtimeportfolio.common.exception.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import({
        GlobalExceptionHandler.class,
        RabbitMQConfig.class
})
@SpringBootApplication(scanBasePackages = "com.realtimeportfolio.notification")
public class NotificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationApplication.class, args);
    }
}
