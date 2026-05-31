package com.portfolio.client;

import org.portfolio.dto.StockTickerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "stock-market-service")
public interface StackMarketClient {

    @GetMapping("/api/stocks")
    List<StockTickerDto> getStockTickers();

    @GetMapping("/api/v1/stocks/price/{tickerSymbol}")
    Optional<StockTickerDto> getCurrentPriceBySymbol(String symbol);
}
