package com.realtimeportfolio.portfolio.service;


import com.realtimeportfolio.common.dto.MenuResponse;

import java.util.List;

public interface UserMenuPermissionService {
    List<MenuResponse> findMenuByUserId(Long userId);
}
