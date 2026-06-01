package com.realtimeportfolio.authentication.service.impl;

import com.realtimeportfolio.authentication.entity.User;

import com.realtimeportfolio.authentication.repo.UserRepository;
import com.realtimeportfolio.authentication.service.JwtService;
import com.realtimeportfolio.authentication.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.common.dto.LoginRequest;
import com.realtimeportfolio.common.dto.LoginResponse;
import com.realtimeportfolio.common.exception.InvalidLoginException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginServiceImpl(UserRepository userRepository,
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

        String token = jwtService.generateToken(user);

        log.info("Login successful for userId={}, email={}", user.getId(), user.getEmail());

        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                token,
                "Bearer",
                "Login successful"
        );
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