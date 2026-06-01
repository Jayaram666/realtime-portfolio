package com.realtimeportfolio.portfolio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "stock_alert_history")
@NoArgsConstructor
@AllArgsConstructor
public class StockAlertHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String tickerSymbol;

    private String alertType;

    private BigDecimal buyingPrice;

    private BigDecimal currentPrice;

    private BigDecimal gainLossPerStock;

    private BigDecimal totalGainLoss;

    private boolean emailSent;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}