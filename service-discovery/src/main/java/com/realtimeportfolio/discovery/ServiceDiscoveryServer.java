package com.realtimeportfolio.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication(scanBasePackages = "com.realtimeportfolio.discovery")
public class ServiceDiscoveryServer {

    public static void main(String[] args) {
        SpringApplication.run(ServiceDiscoveryServer.class, args);
    }
}
