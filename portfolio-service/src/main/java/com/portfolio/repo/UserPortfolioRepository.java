package com.portfolio.repo;

import com.portfolio.entity.UserPortfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPortfolioRepository extends JpaRepository<UserPortfolio, Long> {

    Optional<UserPortfolio> findByUserIdAndTickerSymbolIgnoreCase(
            Long userId,
            String tickerSymbol
    );
    List<UserPortfolio> findByUserIdOrderByCompanyNameAsc(Long userId);

}