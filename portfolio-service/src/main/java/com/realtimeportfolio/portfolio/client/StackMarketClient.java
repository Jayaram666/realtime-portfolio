package com.realtimeportfolio.portfolio.client;

import com.realtimeportfolio.common.dto.StockTickerDto;
import com.realtimeportfolio.common.dto.TickersDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "market-data-service")
public interface StackMarketClient {

    @PostMapping("/api/v1/stocks/tickers")
    List<StockTickerDto> getStockTickers(@RequestBody TickersDto tickers);

    @GetMapping("/api/v1/stocks/price/{tickerSymbol}")
    BigDecimal getCurrentPriceBySymbol(@PathVariable("tickerSymbol") String tickerSymbol);
}
