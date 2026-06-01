package com.realtimeportfolio.portfolio.service;


import com.realtimeportfolio.common.dto.HomePageResponse;

public interface HomePageService {
    HomePageResponse getHomePageData(String userId);
}
