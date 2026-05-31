package com.portfolio.web;

import com.portfolio.service.HomePageService;
import lombok.RequiredArgsConstructor;
import org.portfolio.dto.HomePageResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HomePageController {

    private final HomePageService homePageService;

    @GetMapping("/api/v1/home")
    public HomePageResponse getHomePageData(String userEmail) {
        return homePageService.getHomePageData(userEmail);
    }
}
