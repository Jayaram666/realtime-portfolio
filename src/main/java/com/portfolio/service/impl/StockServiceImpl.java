package com.portfolio.service.impl;

import com.portfolio.client.StockClient;
import com.portfolio.dto.StockTickerDto;
import com.portfolio.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import yahoofinance.Stock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    @Value("${top-stocks}")
    private String[] supportedSymbols;

    private final StockClient stockClient;

    @Override
    public List<StockTickerDto> getStockTickers() {
        return stockClient.getTopStocks(supportedSymbols);
    }
}
