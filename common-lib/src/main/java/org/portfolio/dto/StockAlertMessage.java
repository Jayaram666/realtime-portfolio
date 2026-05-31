package org.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockAlertMessage {

    private Long userId;
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
