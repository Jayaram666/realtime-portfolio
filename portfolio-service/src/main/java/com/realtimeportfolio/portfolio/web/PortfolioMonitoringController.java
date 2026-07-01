package com.realtimeportfolio.portfolio.web;


import com.realtimeportfolio.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.portfolio.service.PortfolioMonitoringService;
import com.realtimeportfolio.common.dto.PortfolioMonitoringResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/portfolio/monitoring")
public class PortfolioMonitoringController {

    private final PortfolioMonitoringService portfolioMonitoringService;

    public PortfolioMonitoringController(
            PortfolioMonitoringService portfolioMonitoringService) {
        this.portfolioMonitoringService = portfolioMonitoringService;
    }

    @GetMapping("/realtime")
    public ApiResponse<PortfolioMonitoringResponse> getRealtimePortfolio(@RequestHeader("X-User-Id") UUID authenticatedUserId, @RequestHeader("X-User-Email") String userEmail, HttpServletRequest httpServletRequest) {
        log.info("Realtime portfolio monitoring API called. userId={}", authenticatedUserId);
        PortfolioMonitoringResponse response = portfolioMonitoringService.getRealtimePortfolio(authenticatedUserId);
        log.info("Realtime portfolio monitoring API completed. userId={}", authenticatedUserId);
        return ApiResponse.<PortfolioMonitoringResponse>builder()
                .data(response)
                .message("Fetched monitoring data successfully")
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .data(response)
                .resource(httpServletRequest.getRequestURI())
                .build();
    }
}
