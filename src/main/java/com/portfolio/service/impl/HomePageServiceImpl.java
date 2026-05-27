package com.portfolio.service.impl;

import com.portfolio.dto.HomePageResponse;
import com.portfolio.dto.UserDto;
import com.portfolio.entity.User;
import com.portfolio.exception.InvalidLoginException;
import com.portfolio.repository.UserRepository;
import com.portfolio.service.HomePageService;
import com.portfolio.service.StockService;
import com.portfolio.service.UserMenuPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HomePageServiceImpl implements HomePageService {

    private final UserMenuPermissionService userMenuPermissionService;
    private final StockService service;
    private final UserRepository userRepository;

    @Override
    public HomePageResponse getHomePageData(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidLoginException("User not found"));
        return new HomePageResponse(
                new UserDto(user.getId(), user.getName(), user.getEmail()),
                userMenuPermissionService.findMenuByUserId(user.getId()),
                service.getStockTickers());
    }
}
