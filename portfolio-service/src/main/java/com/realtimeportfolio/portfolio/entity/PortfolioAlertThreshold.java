package com.realtimeportfolio.portfolio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "portfolio_alert_thresholds",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_stock_threshold",
                        columnNames = {"user_id", "ticker_symbol"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioAlertThreshold {

    @Id
    @GeneratedValue
    private UUID id;

    /*
     * Later this can be mapped with User entity.
     */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "ticker_symbol", nullable = false)
    private String tickerSymbol;

    @Column(name = "upper_threshold_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal upperThresholdPercent;

    @Column(name = "lower_threshold_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal lowerThresholdPercent;

    @Column(name = "upper_alert_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal upperAlertPrice;

    @Column(name = "lower_alert_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal lowerAlertPrice;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void calculateAlertPrices(BigDecimal buyingPrice) {
        BigDecimal hundred = BigDecimal.valueOf(100);

        BigDecimal upperAmount = buyingPrice
                .multiply(upperThresholdPercent)
                .divide(hundred, 4, RoundingMode.HALF_UP);

        BigDecimal lowerAmount = buyingPrice
                .multiply(lowerThresholdPercent)
                .divide(hundred, 4, RoundingMode.HALF_UP);

        this.upperAlertPrice = buyingPrice.add(upperAmount);
        this.lowerAlertPrice = buyingPrice.subtract(lowerAmount);
    }

    public void updateThresholds(
            BigDecimal upperThresholdPercent,
            BigDecimal lowerThresholdPercent,
            BigDecimal buyingPrice
    ) {
        this.upperThresholdPercent = upperThresholdPercent;
        this.lowerThresholdPercent = lowerThresholdPercent;
        calculateAlertPrices(buyingPrice);
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.tickerSymbol = this.tickerSymbol.toUpperCase();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.tickerSymbol = this.tickerSymbol.toUpperCase();
    }
}