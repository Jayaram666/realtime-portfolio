package com.realtimeportfolio.stock;

import com.realtimeportfolio.common.exception.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(GlobalExceptionHandler.class)
@SpringBootApplication(scanBasePackages = "com.realtimeportfolio.stock")
public class StackApplication {

    public static void main(String[] args) {
        SpringApplication.run(StackApplication.class, args);
    }
}
