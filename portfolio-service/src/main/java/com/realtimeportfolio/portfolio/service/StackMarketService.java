package com.realtimeportfolio.portfolio.service;

import com.realtimeportfolio.common.dto.StockTickerDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface StackMarketService {

    List<StockTickerDto> getStockTickers() ;
    Optional<StockTickerDto> getStockPrice(String tickerSymbol);
}