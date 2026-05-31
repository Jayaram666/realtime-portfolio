package com.portfolio.service;


import org.portfolio.dto.HomePageResponse;

public interface HomePageService {
    HomePageResponse getHomePageData(String userId);
}
