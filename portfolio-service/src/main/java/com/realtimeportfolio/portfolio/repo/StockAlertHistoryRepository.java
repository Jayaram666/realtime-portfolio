package com.realtimeportfolio.portfolio.repo;

import com.realtimeportfolio.portfolio.entity.StockAlertHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface StockAlertHistoryRepository extends JpaRepository<StockAlertHistory, Long> {

    boolean existsByUserIdAndTickerSymbolAndAlertTypeAndCreatedAtAfter(
            Long userId,
            String tickerSymbol,
            String alertType,
            LocalDateTime createdAt
    );
}
