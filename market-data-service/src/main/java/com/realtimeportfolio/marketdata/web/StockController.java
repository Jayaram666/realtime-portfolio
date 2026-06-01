package com.realtimeportfolio.marketdata.web;


import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.marketdata.client.RemoteStockClient;
import lombok.RequiredArgsConstructor;
import com.realtimeportfolio.common.dto.StockTickerDto;
import com.realtimeportfolio.common.dto.TickersDto;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
@Slf4j
@RestController
public class StockController {

    private final RemoteStockClient stockClient;

    @PostMapping("/tickers")
    public List<StockTickerDto> getStockTickers(@RequestBody TickersDto tickers) {
        log.info("Market data ticker request received. tickerCount={}", tickers != null && tickers.getTickers() != null ? tickers.getTickers().length : 0);
        List<StockTickerDto> response = stockClient.getTopStocks(tickers);
        log.info("Market data ticker request completed. resultCount={}", response.size());
        return response;
    }

    @GetMapping("/price/{tickerSymbol}")
    public BigDecimal getStockPrice(@PathVariable("tickerSymbol") String tickerSymbol) {
        log.info("Market data price request received. tickerSymbol={}", tickerSymbol);
        BigDecimal price = stockClient.getStockPrice(tickerSymbol).orElseThrow();
        log.info("Market data price request completed. tickerSymbol={}, price={}", tickerSymbol, price);
        return price;
    }
}
