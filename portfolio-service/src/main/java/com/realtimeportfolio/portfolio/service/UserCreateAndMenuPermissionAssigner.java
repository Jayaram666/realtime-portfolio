package com.realtimeportfolio.portfolio.service;

import com.realtimeportfolio.common.dto.UserCreatedEvent;

public interface UserCreateAndMenuPermissionAssigner {
    void assignUserMenuPermissions(UserCreatedEvent userCreatedEvent);
}
