package com.portfolio.web;

import com.portfolio.client.RemoteStockClient;
import lombok.RequiredArgsConstructor;
import org.portfolio.dto.StockTickerDto;
import org.portfolio.dto.TickersDto;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
@RestController
public class StockController {

    private final RemoteStockClient stockClient;

    @PostMapping("/tickers")
    public List<StockTickerDto> getStockTickers(@RequestBody TickersDto tickers) {
        return stockClient.getTopStocks(tickers);
    }

    @GetMapping("/price/{tickerSymbol}")
    public BigDecimal getStockPrice(@PathVariable String tickerSymbol) {
        return stockClient.getStockPrice(tickerSymbol).orElseThrow();
    }
}
