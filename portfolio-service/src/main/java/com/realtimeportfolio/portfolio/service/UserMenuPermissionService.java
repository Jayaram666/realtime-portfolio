package com.realtimeportfolio.portfolio.service;


import com.realtimeportfolio.common.dto.MenuResponse;

import java.util.List;
import java.util.UUID;

public interface UserMenuPermissionService {
    List<MenuResponse> findMenuByUserId(UUID userId);
}
