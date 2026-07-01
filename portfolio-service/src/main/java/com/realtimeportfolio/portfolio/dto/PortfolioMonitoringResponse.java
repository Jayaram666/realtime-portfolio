package com.realtimeportfolio.portfolio.dto;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.realtimeportfolio.common.dto.PortfolioStockValuationResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// This guarantees properties are rendered exactly in this visual layout order in your JSON body response
@JsonPropertyOrder({
        "stocks",
        "totalInvestedValue",
        "totalCurrentValue",
        "totalGainLossAmount",
        "totalGainLossPercent",
        "valuationTimestamp"
})
public record PortfolioMonitoringResponse(
        List<PortfolioStockValuationResponse> stocks,
        BigDecimal totalInvestedValue,
        BigDecimal totalCurrentValue,
        BigDecimal totalGainLossAmount,
        BigDecimal totalGainLossPercent,
        LocalDateTime valuationTimestamp
) {
}
