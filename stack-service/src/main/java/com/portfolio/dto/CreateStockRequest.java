package com.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateStockRequest {

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Ticker symbol is required")
    private String tickerSymbol;

    @NotBlank(message = "Exchange is required")
    private String exchange;

}