package com.realtimeportfolio.portfolio.service.impl;


import com.realtimeportfolio.common.dto.UserCreatedEvent;
import com.realtimeportfolio.common.exception.InvalidLoginException;
import com.realtimeportfolio.portfolio.entity.User;
import com.realtimeportfolio.portfolio.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.portfolio.client.UserClient;
import com.realtimeportfolio.portfolio.service.UserService;
import lombok.RequiredArgsConstructor;
import com.realtimeportfolio.common.dto.UserDto;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserClient userClient;

    private final UserRepository userRepository;

    @Override
    public UserDto getUserDataByEmail(String userEmail) {
        log.debug("Calling authentication-service for user data. userEmail={}", userEmail);
        UserDto response = userRepository.findByEmail(userEmail)
                .map(entity -> new UserDto(entity.getId(), entity.getName(), entity.getEmail()))
                .orElseThrow(() -> new InvalidLoginException("Invalid user"));
        log.debug("authentication-service user data call completed. userEmail={}, found={}", userEmail, response != null);
        return response;
    }

    @Override
    public void createUser(UserCreatedEvent userCreatedEvent) {
        userRepository.save(toEntity(userCreatedEvent));
    }

    private User toEntity(UserCreatedEvent userCreatedEvent) {
        return new User(userCreatedEvent.getUserId(), userCreatedEvent.getName(), userCreatedEvent.getEmail());
    }
}
