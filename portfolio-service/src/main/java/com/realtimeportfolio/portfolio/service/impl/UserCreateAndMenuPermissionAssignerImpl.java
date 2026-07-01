package com.realtimeportfolio.portfolio.service.impl;

import com.realtimeportfolio.common.dto.UserCreatedEvent;
import com.realtimeportfolio.portfolio.entity.MenuPermission;
import com.realtimeportfolio.portfolio.entity.User;
import com.realtimeportfolio.portfolio.entity.UserMenuPermission;
import com.realtimeportfolio.portfolio.repo.MenuPremissionRepository;
import com.realtimeportfolio.portfolio.repo.UserMenuPermissionRepository;
import com.realtimeportfolio.portfolio.repo.UserRepository;
import com.realtimeportfolio.portfolio.service.UserCreateAndMenuPermissionAssigner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserCreateAndMenuPermissionAssignerImpl implements UserCreateAndMenuPermissionAssigner {

    private final UserRepository userRepository;
    private final MenuPremissionRepository menuPremissionRepository; // Note: Check for typo in class name 'Premission'
    private final UserMenuPermissionRepository userMenuPermissionRepository;

    @Override
    @Transactional
    public void assignUserMenuPermissions(UserCreatedEvent userCreatedEvent) {
        User createdUser = userRepository.save(toEntity(userCreatedEvent));

        List<MenuPermission> menuPermissions = menuPremissionRepository.findAll();

        List<UserMenuPermission> userMenuPermissions = menuPermissions.stream()
                .map(menuPermission -> {
                    UserMenuPermission userMenuPermission = new UserMenuPermission();
                    userMenuPermission.setUser(createdUser);
                    userMenuPermission.setMenuPermission(menuPermission);
                    return userMenuPermission;
                })
                .toList();

        // 4. Save the collection
        userMenuPermissionRepository.saveAll(userMenuPermissions);
    }

    private User toEntity(UserCreatedEvent userCreatedEvent) {
        return new User(userCreatedEvent.getUserId(), userCreatedEvent.getName(), userCreatedEvent.getEmail());
    }
}
