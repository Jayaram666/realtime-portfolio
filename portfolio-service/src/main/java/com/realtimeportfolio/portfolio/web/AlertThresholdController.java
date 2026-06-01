package com.realtimeportfolio.portfolio.web;



import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.portfolio.service.AlertThresholdService;
import jakarta.validation.Valid;

import com.realtimeportfolio.common.dto.AlertThresholdRequest;
import com.realtimeportfolio.common.dto.AlertThresholdResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/portfolio/alerts/thresholds")
public class AlertThresholdController {

    private final AlertThresholdService alertThresholdService;

    public AlertThresholdController(AlertThresholdService alertThresholdService) {
        this.alertThresholdService = alertThresholdService;
    }

    @PostMapping
    public AlertThresholdResponse createOrUpdateThreshold(
            @Valid @RequestBody AlertThresholdRequest request,@RequestHeader("X-User-Id") Long userId
    ) {
        /*
         * For now hardcoded.
         * Later get this from Spring Security JWT principal.
         */

        log.info("Create/update alert threshold API called. userId={}, tickerSymbol={}", userId, request.getTickerSymbol());

        AlertThresholdResponse response = alertThresholdService.createOrUpdateThreshold(userId, request);
        log.info("Create/update alert threshold API completed. userId={}, tickerSymbol={}", userId, response.getTickerSymbol());
        return response;
    }

    @GetMapping
    public List<AlertThresholdResponse> getMyThresholds() {
        Long authenticatedUserId = 1L;
        log.info("Get alert thresholds API called. userId={}", authenticatedUserId);

        List<AlertThresholdResponse> response = alertThresholdService.getMyThresholds(authenticatedUserId);
        log.info("Get alert thresholds API completed. userId={}, count={}", authenticatedUserId, response.size());
        return response;
    }
}