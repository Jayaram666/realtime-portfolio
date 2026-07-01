package com.realtimeportfolio.gateway.validator;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PublicEndpointValidator {

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/v1/users/register",
            "/api/v1/auth/login","/api/v1/auth/refresh-token",
            "/actuator/health","/api/v1/users/oauth-sync"
    );

    public boolean isPublicEndpoint(ServerHttpRequest request) {
        String path = request.getURI().getPath();

        boolean publicEndpoint = PUBLIC_ENDPOINTS.stream()
                .anyMatch(path::startsWith);
        log.debug("Gateway public endpoint check. path={}, publicEndpoint={}", path, publicEndpoint);
        return publicEndpoint;
    }
}