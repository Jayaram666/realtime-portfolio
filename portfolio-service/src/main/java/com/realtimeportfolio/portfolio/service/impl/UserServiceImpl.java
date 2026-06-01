package com.realtimeportfolio.portfolio.service.impl;


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

    @Override
    public UserDto getUserDataByEmail(String userEmail) {
        log.debug("Calling authentication-service for user data. userEmail={}", userEmail);
        UserDto response = userClient.getUserByEmail(userEmail);
        log.debug("authentication-service user data call completed. userEmail={}, found={}", userEmail, response != null);
        return response;
    }
}
