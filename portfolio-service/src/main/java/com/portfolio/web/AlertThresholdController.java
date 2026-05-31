package com.portfolio.web;


import com.portfolio.service.AlertThresholdService;
import jakarta.validation.Valid;

import org.portfolio.dto.AlertThresholdRequest;
import org.portfolio.dto.AlertThresholdResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio/alerts/thresholds")
public class AlertThresholdController {

    private final AlertThresholdService alertThresholdService;

    public AlertThresholdController(AlertThresholdService alertThresholdService) {
        this.alertThresholdService = alertThresholdService;
    }

    @PostMapping
    public AlertThresholdResponse createOrUpdateThreshold(
            @Valid @RequestBody AlertThresholdRequest request
    ) {
        /*
         * For now hardcoded.
         * Later get this from Spring Security JWT principal.
         */
        Long authenticatedUserId = 1L;

        return alertThresholdService.createOrUpdateThreshold(authenticatedUserId, request);
    }

    @GetMapping
    public List<AlertThresholdResponse> getMyThresholds() {
        Long authenticatedUserId = 1L;

        return alertThresholdService.getMyThresholds(authenticatedUserId);
    }
}