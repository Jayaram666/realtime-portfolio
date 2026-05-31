package com.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStockRequest {
    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Ticker symbol is required")
    private String tickerSymbol;

    @NotBlank(message = "Exchange is required")
    private String exchange;

    @NotNull(message = "Active status is required")
    private Boolean active;
}
