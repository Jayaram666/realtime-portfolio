package com.realtimeportfolio.authentication.service.impl;

import com.realtimeportfolio.authentication.dto.TokenRefreshRequest;
import com.realtimeportfolio.authentication.dto.TokenRefreshResponse;
import com.realtimeportfolio.authentication.entity.User;

import com.realtimeportfolio.authentication.exception.TokenRefreshException;
import com.realtimeportfolio.authentication.repo.UserRepository;
import com.realtimeportfolio.authentication.service.JwtService;
import com.realtimeportfolio.authentication.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.common.dto.LoginRequest;
import com.realtimeportfolio.common.dto.LoginResponse;
import com.realtimeportfolio.authentication.exception.InvalidLoginException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticationServiceImpl(UserRepository userRepository,
                                     PasswordEncoder passwordEncoder,
                                     JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    @Override
    public LoginResponse login(LoginRequest request) {

        log.info("Login attempt started for email: {}", request != null ? request.getEmail() : null);

        validateRequest(request);

        String normalizedEmail = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> {
                    log.warn("Login failed. User not found for email: {}", normalizedEmail);
                    return new InvalidLoginException("Invalid email or password");
                });

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            log.warn("Login failed. Invalid password for email: {}", normalizedEmail);
            throw new InvalidLoginException("Invalid email or password");
        }

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        log.info("Login successful for userId={}, email={}", user.getId(), user.getEmail());

        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                accessToken,
                refreshToken,
                "Bearer",
                "Login successful"
        );
    }

    @Transactional(readOnly = true)
    @Override
    public TokenRefreshResponse refreshAccessToken(TokenRefreshRequest request) {
        if (request == null || request.getRefreshToken() == null || request.getRefreshToken().trim().isEmpty()) {
            throw new TokenRefreshException("Refresh token missing from request");
        }
        String refreshToken = request.getRefreshToken().trim();

        if (!jwtService.isTokenValid(refreshToken)) {
            log.warn("Token refresh failed: Provided token is expired or structurally invalid.");
            throw new TokenRefreshException("Invalid or expired refresh token");
        }
        String email = jwtService.extractEmail(refreshToken);
        // 3. Ensure the user still exists actively in the database
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Token refresh failed: User associated with token no longer exists. Email={}", email);
                    return new TokenRefreshException("User not found for the provided token");
                });

        // 4. Issue a fresh short-lived access token
        String newAccessToken = jwtService.generateToken(user);

        log.info("Access token successfully refreshed for userId={}", user.getId());

        return TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // Return same token, or 'newRefreshToken' if rotating tokens
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpirationInSeconds()) // Dynamic duration from JWT config
                .build();
    }

    private void validateRequest(LoginRequest request) {

        if (request == null) {
            throw new InvalidLoginException("Request body cannot be empty");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new InvalidLoginException("Email is required");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new InvalidLoginException("Password is required");
        }
    }
}