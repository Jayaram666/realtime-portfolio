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
@Table(name = "user_portfolios")
@NoArgsConstructor
@AllArgsConstructor
public class UserPortfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "company_name", nullable = false)
    private String companyName;
    @Column(name = "ticker_symbol", nullable = false)
    private String tickerSymbol;
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    @Column(name = "buying_price", nullable = false)
    private BigDecimal buyingPrice;
    @Column(name = "total_invested_amount", nullable = false)
    private BigDecimal totalInvestedAmount;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void  prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateTotalInvestedAmount();
    }

    private void calculateTotalInvestedAmount() {
        if (quantity != null && buyingPrice != null) {
            totalInvestedAmount = buyingPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
    @PostUpdate
    public void postUpdate() {
        updatedAt = LocalDateTime.now();
    }
    public void updatePortfolio(Integer quantity, BigDecimal buyingPrice) {
        this.quantity = quantity;
        this.buyingPrice = buyingPrice;
        calculateTotalInvestedAmount();
    }

    public UserPortfolio(Long id, Long userId, String companyName, String tickerSymbol, Integer quantity, BigDecimal buyingPrice) {
        this.userId = userId;
        this.companyName = companyName;
        this.tickerSymbol = tickerSymbol;
    }
}
