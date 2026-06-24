package com.realtimeportfolio.authentication.mapper;

import com.realtimeportfolio.authentication.entity.User;
import com.realtimeportfolio.common.dto.UserDto;

public class UserMapper {

    public static UserDto userDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}
