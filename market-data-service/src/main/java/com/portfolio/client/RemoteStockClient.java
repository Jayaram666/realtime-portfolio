package com.portfolio.client;


import org.portfolio.dto.StockTickerDto;
import org.portfolio.dto.TickersDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface RemoteStockClient {
    List<StockTickerDto> getTopStocks(TickersDto symbols) ;
    Optional<BigDecimal> getStockPrice(String tickerSymbol);
}
