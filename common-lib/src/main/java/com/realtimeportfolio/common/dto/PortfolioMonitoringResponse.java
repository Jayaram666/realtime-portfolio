package com.realtimeportfolio.common.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PortfolioMonitoringResponse {

    private BigDecimal totalInvestedValue;
    private BigDecimal totalCurrentValue;
    private BigDecimal totalGainLossAmount;
    private BigDecimal totalGainLossPercent;

    private LocalDateTime responseGeneratedAt;

    private List<PortfolioStockValuationResponse> stocks;

    public PortfolioMonitoringResponse(
            BigDecimal totalInvestedValue,
            BigDecimal totalCurrentValue,
            BigDecimal totalGainLossAmount,
            BigDecimal totalGainLossPercent,
            LocalDateTime responseGeneratedAt,
            List<PortfolioStockValuationResponse> stocks
    ) {
        this.totalInvestedValue = totalInvestedValue;
        this.totalCurrentValue = totalCurrentValue;
        this.totalGainLossAmount = totalGainLossAmount;
        this.totalGainLossPercent = totalGainLossPercent;
        this.responseGeneratedAt = responseGeneratedAt;
        this.stocks = stocks;
    }

    public BigDecimal getTotalInvestedValue() {
        return totalInvestedValue;
    }

    public BigDecimal getTotalCurrentValue() {
        return totalCurrentValue;
    }

    public BigDecimal getTotalGainLossAmount() {
        return totalGainLossAmount;
    }

    public BigDecimal getTotalGainLossPercent() {
        return totalGainLossPercent;
    }

    public LocalDateTime getResponseGeneratedAt() {
        return responseGeneratedAt;
    }

    public List<PortfolioStockValuationResponse> getStocks() {
        return stocks;
    }
}