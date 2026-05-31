package com.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlertThresholdResponse {

    private Long id;
    private String tickerSymbol;
    private BigDecimal buyingPrice;
    private BigDecimal upperThresholdPercent;
    private BigDecimal lowerThresholdPercent;
    private BigDecimal upperAlertPrice;
    private BigDecimal lowerAlertPrice;
    private boolean active;
}