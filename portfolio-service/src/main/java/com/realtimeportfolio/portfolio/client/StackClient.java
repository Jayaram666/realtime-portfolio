package com.realtimeportfolio.portfolio.client;

import com.realtimeportfolio.common.dto.StockResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "stack-service")
public interface StackClient {

    @GetMapping("/api/stocks/{tickerSymbol}")
    StockResponse getStockByTickerSymbol(@PathVariable("tickerSymbol") String tickerSymbol);
}
