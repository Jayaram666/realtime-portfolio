package com.realtimeportfolio.authentication.service.impl;

import com.realtimeportfolio.authentication.dto.OAuthSyncRequest;
import com.realtimeportfolio.authentication.dto.UserSyncResponse;
import com.realtimeportfolio.authentication.producer.UserCreatedProducer;
import com.realtimeportfolio.common.dto.UserCreatedEvent;
import com.realtimeportfolio.common.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.authentication.entity.User;
import com.realtimeportfolio.authentication.repo.UserRepository;
import com.realtimeportfolio.authentication.service.UserRegistrationService;
import com.realtimeportfolio.authentication.validator.UserRegistrationValidator;
import com.realtimeportfolio.common.dto.UserRegistrationRequest;
import com.realtimeportfolio.common.dto.UserRegistrationResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRegistrationServiceImpl implements UserRegistrationService {
    private final UserRepository userRepository;
    private final UserRegistrationValidator validator;
    private final PasswordEncoder passwordEncoder;
    private final UserCreatedProducer userCreatedProducer;

    @Transactional
    @Override
    public UserRegistrationResponse register(UserRegistrationRequest request) {

        log.info("User registration started for email: {}", request != null ? request.getEmail() : null);

        List<String> validationErrors = validator.validate(request);

        if (!validationErrors.isEmpty()) {
            throw new ValidationException(validationErrors);
        }

        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ValidationException("User name or email already exists in DB");
        }

        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getName().trim(),
                normalizedEmail,
                encryptedPassword
        );

        User savedUser = userRepository.save(user);
        userCreatedProducer.produce(new UserCreatedEvent(savedUser.getId(), savedUser.getName(), savedUser.getEmail()));
        log.info("User registration completed successfully. userId={}, email={}",
                savedUser.getId(), savedUser.getEmail());

        return new UserRegistrationResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                "User registered successfully"
        );
    }

    @Override
    public UserSyncResponse processOAuthUser(OAuthSyncRequest oAuthSyncRequest) {

        if (userRepository.existsByEmail(oAuthSyncRequest.getEmail())) {
            return new UserSyncResponse();
        }
        String encryptedPassword = passwordEncoder.encode(UUID.randomUUID().toString());
        User user = new User(
                oAuthSyncRequest.getUserId(),
                oAuthSyncRequest.getName(),
                oAuthSyncRequest.getEmail(),
                encryptedPassword
        );

        User savedUser = userRepository.save(user);
        userCreatedProducer.produce(new UserCreatedEvent(savedUser.getId(), savedUser.getName(), savedUser.getEmail()));
        log.info("User registration completed successfully in oauth2 . userId={}, email={}",
                savedUser.getId(), savedUser.getEmail());

        return new UserSyncResponse(savedUser.getId(), savedUser.getEmail(), savedUser.getName());
    }
}