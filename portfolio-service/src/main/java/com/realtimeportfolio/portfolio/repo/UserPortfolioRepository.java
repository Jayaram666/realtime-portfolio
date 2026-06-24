package com.realtimeportfolio.portfolio.repo;

import com.realtimeportfolio.portfolio.entity.UserPortfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserPortfolioRepository extends JpaRepository<UserPortfolio, UUID> {

    Optional<UserPortfolio> findByUserIdAndTickerSymbolIgnoreCase(
            UUID userId,
            String tickerSymbol
    );
    List<UserPortfolio> findByUserIdOrderByCompanyNameAsc(UUID userId);

}