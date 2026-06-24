package com.realtimeportfolio.portfolio.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "stacks",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"ticker_symbol"})
        })
@NoArgsConstructor
@AllArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;
    @Column(name = "company_name", nullable = false)
    private String companyName;
    @Column(name = "ticker_symbol", nullable = false, unique = true)
    private String tickerSymbol;
    @Column(nullable = false)
    private boolean active;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.tickerSymbol = this.tickerSymbol.toUpperCase();
    }

    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.tickerSymbol = this.tickerSymbol.toUpperCase();
    }


}
