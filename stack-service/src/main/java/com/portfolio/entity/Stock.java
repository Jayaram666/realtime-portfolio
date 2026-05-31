package com.portfolio.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "stocks",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_stock_ticker_exchange",
                        columnNames = {"ticker_symbol", "exchange"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "ticker_symbol", nullable = false, length = 50)
    private String tickerSymbol;

    @Column(name = "exchange", nullable = false, length = 50)
    private String exchange;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.tickerSymbol != null) {
            this.tickerSymbol = this.tickerSymbol.trim().toUpperCase();
        }

        if (this.exchange != null) {
            this.exchange = this.exchange.trim().toUpperCase();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();

        if (this.tickerSymbol != null) {
            this.tickerSymbol = this.tickerSymbol.trim().toUpperCase();
        }

        if (this.exchange != null) {
            this.exchange = this.exchange.trim().toUpperCase();
        }
    }
}