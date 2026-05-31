package com.portfolio.service;

import org.portfolio.dto.AlertThresholdRequest;
import org.portfolio.dto.AlertThresholdResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AlertThresholdService {
    @Transactional
    AlertThresholdResponse createOrUpdateThreshold(
            Long userId,
            AlertThresholdRequest request
    );

    @Transactional(readOnly = true)
    List<AlertThresholdResponse> getMyThresholds(Long userId);
}
