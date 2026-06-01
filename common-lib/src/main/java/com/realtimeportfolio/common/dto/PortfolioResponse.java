package com.realtimeportfolio.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioResponse {

    private Long id;
    private String companyName;
    private String tickerSymbol;
    private Integer quantity;
    private BigDecimal buyingPrice;
    private BigDecimal totalInvestedAmount;

}
