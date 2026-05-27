package com.portfolio.service;

import com.portfolio.dto.MenuResponse;

import java.util.List;

public interface UserMenuPermissionService {
    List<MenuResponse> findMenuByUserId(Long userId);
}
