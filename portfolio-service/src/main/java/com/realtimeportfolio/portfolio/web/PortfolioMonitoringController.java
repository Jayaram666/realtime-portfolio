package com.realtimeportfolio.portfolio.web;


import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.portfolio.service.PortfolioMonitoringService;
import com.realtimeportfolio.common.dto.PortfolioMonitoringResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
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
    public PortfolioMonitoringResponse getRealtimePortfolio(@RequestHeader("X-User-Id") Long authenticatedUserId, @RequestHeader("X-User-Email") String userEmai) {


        log.info("Realtime portfolio monitoring API called. userId={}", authenticatedUserId);

        PortfolioMonitoringResponse response = portfolioMonitoringService.getRealtimePortfolio(authenticatedUserId);
        log.info("Realtime portfolio monitoring API completed. userId={}", authenticatedUserId);
        return response;
    }
}
