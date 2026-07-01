package com.realtimeportfolio.portfolio.repo;

import com.realtimeportfolio.portfolio.entity.UserPortfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserPortfolioRepository extends JpaRepository<UserPortfolio, UUID> {

    Optional<UserPortfolio> findByUserIdAndTickerSymbolIgnoreCase(
            UUID userId,
            String tickerSymbol
    );
    List<UserPortfolio> findByUserIdOrderByCompanyNameAsc(UUID userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserPortfolio p WHERE p.userId = :userId AND LOWER(p.tickerSymbol) = LOWER(:tickerSymbol)")
    int deleteByUserIdAndTickerSymbolIgnoreCase(UUID userId, String tickerSymbol);
}