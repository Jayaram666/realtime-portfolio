package com.realtimeportfolio.gateway.filter;


import com.realtimeportfolio.gateway.util.GatewaySecurityConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.gateway.validator.JwtTokenValidator;
import com.realtimeportfolio.gateway.validator.PublicEndpointValidator;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;

import org.springframework.core.Ordered;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

import org.springframework.stereotype.Component;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    private final JwtTokenValidator jwtTokenValidator;
    private final PublicEndpointValidator publicEndpointValidator;
    private final GatewaySecurityConfig gatewaySecurityConfig;


    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain
    ) {
        ServerHttpRequest request = exchange.getRequest();

        if (gatewaySecurityConfig.isPublic(request.getURI().getPath())) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtTokenValidator.validateAndGetClaims(token);

            String userId = String.valueOf(claims.get("userId"));
            String email = claims.getSubject();
            String roles = String.valueOf(claims.get("roles"));

            /*
             * Important security step:
             * Remove any incoming user headers from external client,
             * then add trusted headers from validated JWT.
             */
            ServerHttpRequest mutatedRequest = request.mutate()
                    .headers(headers -> {
                        headers.remove("X-User-Id");
                        headers.remove("X-User-Email");
                        headers.remove("X-User-Roles");
                    })
                    .header("X-User-Id", userId)
                    .header("X-User-Email", email)
                    .header("X-AUTHORIZATION_HEADER", token)
                    .header("X-User-Roles", roles)
                    .build();

            log.info("JWT validated successfully. userId={}, email={}", userId, email);

            return chain.filter(
                    exchange.mutate()
                            .request(mutatedRequest)
                            .build()
            );

        } catch (Exception ex) {
            log.error("JWT validation failed", ex);
            return unauthorized(exchange, "Invalid or expired token");
        }
    }

    private Mono<Void> unauthorized(
            ServerWebExchange exchange,
            String message
    ) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);

        response.getHeaders().add("Content-Type", "application/json");

        String body = """
                {
                  "status": 401,
                  "error": "UNAUTHORIZED",
                  "message": "%s"
                }
                """.formatted(message);

        byte[] bytes = body.getBytes();

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(bytes))
        );
    }

    @Override
    public int getOrder() {
        return -1;
    }
}