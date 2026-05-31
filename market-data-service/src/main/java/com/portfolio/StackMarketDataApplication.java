package com.portfolio;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.portfolio",
        "org.portfolio"
})
public class StackMarketDataApplication {

    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(StackMarketDataApplication.class, args);
    }
}
