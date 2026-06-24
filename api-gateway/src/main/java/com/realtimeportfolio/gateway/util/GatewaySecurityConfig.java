package com.realtimeportfolio.gateway.util;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

import java.util.HashSet;
import java.util.Set;

@Configuration
@ConfigurationProperties(prefix = "gateway.security")
public class GatewaySecurityConfig {

    @Setter
    private Set<String> publicUrls = new HashSet<>();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public boolean isPublic(String requestPath) {
        // Regex turns "/api/v1/auth/login" or "/api/v2/auth/login" into "/auth/login"
        String sanitizedPath = requestPath.replaceAll("^/api/v\\d+", "");

        return publicUrls.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, sanitizedPath));
    }
}
