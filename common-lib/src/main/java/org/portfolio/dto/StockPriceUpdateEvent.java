package org.portfolio.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockPriceUpdateEvent {

    private String tickerSymbol;
    private BigDecimal currentPrice;
    private LocalDateTime eventTime;
}
