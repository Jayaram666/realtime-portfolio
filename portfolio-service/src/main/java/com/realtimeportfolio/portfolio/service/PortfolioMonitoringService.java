package com.realtimeportfolio.portfolio.service;

import com.realtimeportfolio.common.dto.PortfolioMonitoringResponse;

public interface PortfolioMonitoringService {

    PortfolioMonitoringResponse getRealtimePortfolio(Long userId);
}
