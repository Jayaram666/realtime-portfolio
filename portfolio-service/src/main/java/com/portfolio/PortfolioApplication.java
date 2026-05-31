package com.portfolio;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {
        "com.portfolio",
        "org.portfolio"
})
@EnableFeignClients(basePackages = "com.portfolio.client")
public class PortfolioApplication {

    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(PortfolioApplication.class, args);
    }
}