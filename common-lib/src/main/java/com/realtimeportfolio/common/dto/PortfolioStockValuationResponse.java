package com.realtimeportfolio.common.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PortfolioStockValuationResponse {

    private String companyName;
    private String tickerSymbol;

    private Integer quantity;
    private BigDecimal buyingPrice;
    private BigDecimal currentPrice;

    private BigDecimal investedValue;
    private BigDecimal currentValue;

    private BigDecimal gainLossAmount;
    private BigDecimal gainLossPercent;

    private boolean upperThresholdCrossed;
    private boolean lowerThresholdCrossed;

    private boolean currentPriceAvailable;
    private LocalDateTime priceLastUpdatedAt;

    public PortfolioStockValuationResponse(
            String companyName,
            String tickerSymbol,
            Integer quantity,
            BigDecimal buyingPrice,
            BigDecimal currentPrice,
            BigDecimal investedValue,
            BigDecimal currentValue,
            BigDecimal gainLossAmount,
            BigDecimal gainLossPercent,
            boolean upperThresholdCrossed,
            boolean lowerThresholdCrossed,
            boolean currentPriceAvailable,
            LocalDateTime priceLastUpdatedAt
    ) {
        this.companyName = companyName;
        this.tickerSymbol = tickerSymbol;
        this.quantity = quantity;
        this.buyingPrice = buyingPrice;
        this.currentPrice = currentPrice;
        this.investedValue = investedValue;
        this.currentValue = currentValue;
        this.gainLossAmount = gainLossAmount;
        this.gainLossPercent = gainLossPercent;
        this.upperThresholdCrossed = upperThresholdCrossed;
        this.lowerThresholdCrossed = lowerThresholdCrossed;
        this.currentPriceAvailable = currentPriceAvailable;
        this.priceLastUpdatedAt = priceLastUpdatedAt;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getBuyingPrice() {
        return buyingPrice;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public BigDecimal getInvestedValue() {
        return investedValue;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public BigDecimal getGainLossAmount() {
        return gainLossAmount;
    }

    public BigDecimal getGainLossPercent() {
        return gainLossPercent;
    }

    public boolean isUpperThresholdCrossed() {
        return upperThresholdCrossed;
    }

    public boolean isLowerThresholdCrossed() {
        return lowerThresholdCrossed;
    }

    public boolean isCurrentPriceAvailable() {
        return currentPriceAvailable;
    }

    public LocalDateTime getPriceLastUpdatedAt() {
        return priceLastUpdatedAt;
    }
}