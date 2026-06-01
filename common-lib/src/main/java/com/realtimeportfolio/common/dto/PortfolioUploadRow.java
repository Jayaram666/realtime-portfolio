package com.realtimeportfolio.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioUploadRow {

    private int rowNumber;
    private String stockName;
    private String tickerSymbol;
    private Integer quantity;
    private BigDecimal buyingPrice;
}
