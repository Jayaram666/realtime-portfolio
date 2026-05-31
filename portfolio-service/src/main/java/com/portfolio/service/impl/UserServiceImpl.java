package com.portfolio.service.impl;

import com.portfolio.client.UserClient;
import com.portfolio.service.UserService;
import lombok.RequiredArgsConstructor;
import org.portfolio.dto.UserDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserClient userClient;

    @Override
    public UserDto getUserDataByEmail(String userEmail) {
        return userClient.getUserByEmail(userEmail);
    }
}
