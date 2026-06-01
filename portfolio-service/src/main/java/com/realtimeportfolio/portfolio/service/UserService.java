package com.realtimeportfolio.portfolio.service;

import com.realtimeportfolio.common.dto.UserDto;

public interface UserService {

    UserDto getUserDataByEmail(String userEmail);

}
