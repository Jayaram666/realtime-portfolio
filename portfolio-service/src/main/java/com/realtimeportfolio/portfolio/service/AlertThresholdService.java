package com.realtimeportfolio.portfolio.service;

import com.realtimeportfolio.common.dto.AlertThresholdRequest;
import com.realtimeportfolio.common.dto.AlertThresholdResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface AlertThresholdService {

    AlertThresholdResponse createOrUpdateThreshold(
            UUID userId,
            AlertThresholdRequest request
    );

    List<AlertThresholdResponse> getMyThresholds(UUID userId);

}
