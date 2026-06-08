package com.realtimeportfolio.marketdata.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(UpstoxProperties.class)
public class UpstoxConfig {
}