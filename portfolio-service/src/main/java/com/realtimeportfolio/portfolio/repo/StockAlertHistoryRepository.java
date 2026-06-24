package com.realtimeportfolio.portfolio.repo;

import com.realtimeportfolio.portfolio.entity.StockAlertHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface StockAlertHistoryRepository extends JpaRepository<StockAlertHistory, UUID> {

    boolean existsByUserIdAndTickerSymbolAndAlertTypeAndCreatedAtAfter(
            UUID userId,
            String tickerSymbol,
            String alertType,
            LocalDateTime createdAt
    );
}
