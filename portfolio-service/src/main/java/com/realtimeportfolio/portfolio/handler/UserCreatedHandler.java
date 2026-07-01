package com.realtimeportfolio.portfolio.handler;

import com.realtimeportfolio.common.dto.UserCreatedEvent;
import com.realtimeportfolio.portfolio.service.UserCreateAndMenuPermissionAssigner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCreatedHandler {

    private final UserCreateAndMenuPermissionAssigner userCreateAndMenuPermissionAssigner;

    public void handleUserCreated(UserCreatedEvent userCreatedEvent) {
        userCreateAndMenuPermissionAssigner.assignUserMenuPermissions(userCreatedEvent);
    }
}
