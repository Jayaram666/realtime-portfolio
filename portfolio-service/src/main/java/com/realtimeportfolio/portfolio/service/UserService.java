package com.realtimeportfolio.portfolio.service;

import com.realtimeportfolio.common.dto.UserCreatedEvent;
import com.realtimeportfolio.common.dto.UserDto;

public interface UserService {

    UserDto getUserDataByEmail(String userEmail);
    void createUser(UserCreatedEvent userCreatedEvent);
}
