package org.portfolio.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlertThresholdRequest {

    @NotBlank(message = "Ticker symbol is required")
    private String tickerSymbol;

    @NotNull(message = "Upper threshold percent is required")
    @DecimalMin(value = "0.01", message = "Upper threshold percent must be greater than zero")
    private BigDecimal upperThresholdPercent;

    @NotNull(message = "Lower threshold percent is required")
    @DecimalMin(value = "0.01", message = "Lower threshold percent must be greater than zero")
    private BigDecimal lowerThresholdPercent;

}