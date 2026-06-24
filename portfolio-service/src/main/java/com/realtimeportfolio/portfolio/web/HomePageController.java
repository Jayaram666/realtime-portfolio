package com.realtimeportfolio.portfolio.web;


import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.portfolio.service.HomePageService;
import lombok.RequiredArgsConstructor;
import com.realtimeportfolio.common.dto.HomePageResponse;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolio")
public class HomePageController {

    private final HomePageService homePageService;

    @GetMapping("/home")
    public HomePageResponse getHomePageData(@RequestHeader("X-User-Id") UUID userId, @RequestHeader("X-User-Email") String userEmail) {
        log.info("Home page API called. userEmail={}", userEmail);
        HomePageResponse response = homePageService.getHomePageData(userEmail);
        log.info("Home page API completed. userEmail={}", userEmail);
        return response;
    }
}
