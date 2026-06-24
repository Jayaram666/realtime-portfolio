package com.realtimeportfolio.portfolio.repo;

import com.realtimeportfolio.portfolio.entity.PortfolioAlertThreshold;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PortfolioAlertThresholdRepository
        extends JpaRepository<PortfolioAlertThreshold, UUID> {

    Optional<PortfolioAlertThreshold> findByUserIdAndTickerSymbolIgnoreCase(
            UUID userId,
            String tickerSymbol
    );

    List<PortfolioAlertThreshold> findByUserIdAndActiveTrueOrderByTickerSymbolAsc(UUID userId);
}