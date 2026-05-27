package com.portfolio.controller;

import com.portfolio.dto.HomePageResponse;
import com.portfolio.service.HomePageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HomePageController {

    private final HomePageService homePageService;

    @GetMapping("/api/v1/home")
    public HomePageResponse getHomePageData(Authentication authentication) {
        String userEmail = authentication.getName();
        return homePageService.getHomePageData(userEmail);
    }
}
