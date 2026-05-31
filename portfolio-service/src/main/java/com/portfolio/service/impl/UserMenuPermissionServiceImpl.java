package com.portfolio.service.impl;

import com.portfolio.repo.UserMenuPermissionRepository;
import com.portfolio.service.UserMenuPermissionService;
import lombok.RequiredArgsConstructor;
import org.portfolio.dto.MenuPermissionRow;
import org.portfolio.dto.MenuResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserMenuPermissionServiceImpl implements UserMenuPermissionService {

    private final UserMenuPermissionRepository menuRepository;

    @Override
    public List<MenuResponse> findMenuByUserId(Long userId) {
        List<MenuPermissionRow> menuPermissionRows = menuRepository.findMenuPermissionsByUserId(userId);
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
