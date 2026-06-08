package com.realtimeportfolio.stock.repo;



import com.realtimeportfolio.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    List<Stock> findByActiveTrueOrderByCompanyNameAsc();

    Optional<Stock> findByTickerSymbolIgnoreCaseAndActiveTrue(String tickerSymbol);

    boolean existsByTickerSymbolIgnoreCaseAndActiveTrue(String tickerSymbol);

    boolean existsByTickerSymbolIgnoreCaseAndExchangeIgnoreCase(
            String tickerSymbol
    );

    boolean existsByTickerSymbolIgnoreCaseAndExchangeIgnoreCaseAndIdNot(
            String tickerSymbol,
            String exchange,
            Long id
    );

    @Query("""
            select s
            from Stock s
            where s.active = true
            and (
                lower(s.companyName) like lower(concat('%', :keyword, '%'))
                or lower(s.tickerSymbol) like lower(concat('%', :keyword, '%'))
                or lower(s.exchange) like lower(concat('%', :keyword, '%'))
            )
            order by s.companyName asc
            """)
    List<Stock> searchActiveStocks(@Param("keyword") String keyword);
}