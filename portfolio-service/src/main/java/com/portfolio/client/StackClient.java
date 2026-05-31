package com.portfolio.client;

import org.portfolio.dto.StockResponse;
import org.portfolio.dto.StockTickerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "stock-service")
public interface StackClient {

    @GetMapping("/api/stocks/{tickerSymbol}")
    List<StockResponse> getStockByTickerSymbol(String tickerSymbol);
}
