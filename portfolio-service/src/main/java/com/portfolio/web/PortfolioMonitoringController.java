package com.portfolio.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portfolio/monitoring")
public class PortfolioMonitoringController {

    private final PortfolioMonitoringService portfolioMonitoringService;

    public PortfolioMonitoringController(
            PortfolioMonitoringService portfolioMonitoringService
    ) {
        this.portfolioMonitoringService = portfolioMonitoringService;
    }

    @GetMapping("/realtime")
    public PortfolioMonitoringResponse getRealtimePortfolio() {

        /*
         * Later replace with JWT authenticated user.
         */
        Long authenticatedUserId = 1L;

        return portfolioMonitoringService.getRealtimePortfolio(authenticatedUserId);
    }
}
