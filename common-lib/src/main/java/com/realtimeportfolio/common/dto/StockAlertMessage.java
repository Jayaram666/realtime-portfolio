package com.realtimeportfolio.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockAlertMessage {

    private UUID userId;
    private String userEmail;

    private String tickerSymbol;
    private String companyName;

    private Integer quantity;
    private BigDecimal buyingPrice;
    private BigDecimal currentPrice;

    private BigDecimal gainLossPerStock;
    private BigDecimal totalGainLoss;

    private String alertType;

}
