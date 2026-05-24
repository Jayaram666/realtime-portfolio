package com.portfolio.service;

import com.portfolio.dto.UserRegistrationRequest;
import com.portfolio.dto.UserRegistrationResponse;
import com.portfolio.entity.User;
import com.portfolio.exception.UserRegistrationException;
import com.portfolio.repository.UserRepository;
import com.portfolio.validator.UserRegistrationValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class UserRegistrationService {

    private static final Logger log = LoggerFactory.getLogger(UserRegistrationService.class);

    private final UserRepository userRepository;
    private final UserRegistrationValidator validator;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationService(UserRepository userRepository,
                                   UserRegistrationValidator validator,
                                   PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserRegistrationResponse register(UserRegistrationRequest request) {

        log.info("User registration started for email: {}", request != null ? request.getEmail() : null);

        List<String> validationErrors = validator.validate(request);

        if (!validationErrors.isEmpty()) {
            throw new UserRegistrationException(validationErrors);
        }

        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new UserRegistrationException(
                    Collections.singletonList("User name or email already exists in DB")
            );
        }

        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getName().trim(),
                normalizedEmail,
                encryptedPassword
        );

        User savedUser = userRepository.save(user);

        log.info("User registration completed successfully. userId={}, email={}",
                savedUser.getId(), savedUser.getEmail());

        return new UserRegistrationResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                "User registered successfully"
        );
    }
}