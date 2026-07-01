package com.realtimeportfolio.gateway.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final String JWT_SECRET = "my-super-secure-secret-key-with-32-chars-minimum";
    private final SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));

    // Initialize WebClient targeting your backend user registration endpoint
    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080")
            .build();

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/auth/**", "/oauth2/**", "/login/**", "/api/v1/users/register", "/api/v1/auth/login", "/api/v1/auth/refresh-token").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtDecoder(reactiveJwtDecoder()))
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .oauth2Login(oauth2 -> oauth2
                        .authenticationSuccessHandler((webFilterExchange, authentication) -> {
                            var exchange = webFilterExchange.getExchange();
                            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

                            String email = oAuth2User.getAttribute("email");
                            String name = oAuth2User.getAttribute("name");

                            // Create the payload expected by your registration endpoint
                            Map<String, Object> registrationPayload = new HashMap<>();
                            registrationPayload.put("email", email);
                            registrationPayload.put("name", name);
                            // Add extra placeholder fields if your endpoint requires them (e.g. password, provider)
                            registrationPayload.put("authProvider", "GOOGLE");

                            // 1. Invoke the remote user registration endpoint reactively
                            return webClient.post()
                                    .uri("/api/v1/users/oauth-sync")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(registrationPayload)
                                    .retrieve()
                                    // Handles error codes gracefully or ignores if the user already exists
                                    .onStatus(status -> {
                                                System.out.println("Status :" + status);
                                                return true;
                                            },
                                            clientResponse -> Mono.empty())
                                    .toBodilessEntity()
                                    // 2. Proceed to generate tokens only after the endpoint call finishes
                                    .then(Mono.defer(() -> {
                                        Map<String, Object> claims = new HashMap<>();
                                        claims.put("sub", email);
                                        claims.put("name", name);
                                        claims.put("role", "USER");
                                        assert email != null;
                                        claims.put("userId", UUID.nameUUIDFromBytes(email.getBytes(StandardCharsets.UTF_8)));

                                        String accessToken = Jwts.builder()
                                                .claims(claims)
                                                .issuedAt(new Date())
                                                .expiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000))
                                                .signWith(key, Jwts.SIG.HS256)
                                                .compact();

                                        String refreshToken = Jwts.builder()
                                                .subject(email)
                                                .issuedAt(new Date())
                                                .expiration(new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000))
                                                .signWith(key, Jwts.SIG.HS256)
                                                .compact();

                                        String targetUrl = String.format(
                                                "http://localhost:5173/oauth-callback?token=%s&refreshToken=%s",
                                                accessToken, refreshToken
                                        );

                                        exchange.getResponse().setStatusCode(HttpStatus.FOUND);
                                        exchange.getResponse().getHeaders().setLocation(java.net.URI.create(targetUrl));
                                        return exchange.getResponse().setComplete();
                                    }));
                        })
                );

        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder.withSecretKey(key)
                .macAlgorithm(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256)
                .build();

        OAuth2TokenValidator<Jwt> customValidator = new DelegatingOAuth2TokenValidator<>(
                new JwtTimestampValidator()
        );

        decoder.setJwtValidator(customValidator);
        return decoder;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
