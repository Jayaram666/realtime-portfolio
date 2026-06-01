package com.realtimeportfolio.portfolio.service;

import com.realtimeportfolio.common.dto.AlertThresholdRequest;
import com.realtimeportfolio.common.dto.AlertThresholdResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AlertThresholdService {

    AlertThresholdResponse createOrUpdateThreshold(
            Long userId,
            AlertThresholdRequest request
    );

    List<AlertThresholdResponse> getMyThresholds(Long userId);
}
