package com.realtimeportfolio.portfolio.repo;

import com.realtimeportfolio.portfolio.entity.PortfolioAlertThreshold;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PortfolioAlertThresholdRepository
        extends JpaRepository<PortfolioAlertThreshold, Long> {

    Optional<PortfolioAlertThreshold> findByUserIdAndTickerSymbolIgnoreCase(
            Long userId,
            String tickerSymbol
    );

    List<PortfolioAlertThreshold> findByUserIdAndActiveTrueOrderByTickerSymbolAsc(Long userId);
}