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
public class StockTickerDto {

    private String symbol;
    private BigDecimal currentPrice;
    private BigDecimal previousClosePrice;
    private BigDecimal change;
    private String currency;

}
