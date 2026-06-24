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
public class PortfolioResponse {

    private UUID id;
    private String companyName;
    private String tickerSymbol;
    private Integer quantity;
    private BigDecimal buyingPrice;
    private BigDecimal totalInvestedAmount;

}
