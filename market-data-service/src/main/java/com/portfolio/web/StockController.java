package com.portfolio.web;

import com.portfolio.client.StockClient;
import lombok.RequiredArgsConstructor;
import org.portfolio.dto.StockTickerDto;
import org.portfolio.dto.TickersDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
@RestController
public class StockController {

    private final StockClient stockClient;

    @PostMapping("/tickers")
    public List<StockTickerDto> getStockTickers(@RequestBody TickersDto tickers){
       return stockClient.getTopStocks(tickers);
    }
}
