package com.realtimeportfolio.portfolio.service.impl;


import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.portfolio.repo.UserMenuPermissionRepository;
import com.realtimeportfolio.portfolio.service.UserMenuPermissionService;
import lombok.RequiredArgsConstructor;
import com.realtimeportfolio.common.dto.MenuPermissionRow;
import com.realtimeportfolio.common.dto.MenuResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserMenuPermissionServiceImpl implements UserMenuPermissionService {

    private final UserMenuPermissionRepository menuRepository;

    @Override
    public List<MenuResponse> findMenuByUserId(UUID userId) {
        log.debug("Fetching menu permissions. userId={}", userId);
        List<MenuPermissionRow> menuPermissionRows = menuRepository.findMenuPermissionsByUserId(userId);
        log.debug("Menu permission rows fetched. userId={}, rowCount={}", userId, menuPermissionRows.size());
        if (menuPermissionRows.isEmpty()) {
            return List.of();
        }
        Map<String, MenuResponse.MenuResponseBuilder> menuMap = new HashMap<>();
        for (MenuPermissionRow row : menuPermissionRows) {
            MenuResponse.MenuResponseBuilder menuResponseBuilder =
                    menuMap.get(row.getMenuName());
            if (menuResponseBuilder == null) {
                menuResponseBuilder = MenuResponse.builder()
                        .menuOption(row.getMenuName())
                        .label(row.getMenuLabel());
                menuMap.put(row.getMenuName(), menuResponseBuilder);
            }
            menuResponseBuilder.setPermissions(row.getPermissionCode());
        }
        return menuMap.values().stream()
                .map(MenuResponse.MenuResponseBuilder::build)
                .collect(Collectors.toList());
    }
}
