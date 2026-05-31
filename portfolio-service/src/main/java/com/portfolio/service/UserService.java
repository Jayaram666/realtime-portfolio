package com.portfolio.service;

import org.portfolio.dto.UserDto;

public interface UserService {

    UserDto getUserDataByEmail(String userEmail);

}
