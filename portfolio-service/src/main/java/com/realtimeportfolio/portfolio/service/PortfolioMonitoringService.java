package com.realtimeportfolio.portfolio.service;

import com.realtimeportfolio.common.dto.PortfolioMonitoringResponse;

import java.util.UUID;

public interface PortfolioMonitoringService {

    PortfolioMonitoringResponse getRealtimePortfolio(UUID userId);
}
