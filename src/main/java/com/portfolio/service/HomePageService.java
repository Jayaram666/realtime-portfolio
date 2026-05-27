package com.portfolio.service;

import com.portfolio.dto.HomePageResponse;

public interface HomePageService {
    HomePageResponse getHomePageData(String userId);
}
