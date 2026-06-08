package com.realtimeportfolio.marketdata.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "upstox")
public class UpstoxProperties {

    private String accessToken;
    private String apiVersion = "2.0";
}