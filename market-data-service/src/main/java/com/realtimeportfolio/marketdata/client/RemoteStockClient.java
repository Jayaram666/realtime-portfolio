package com.realtimeportfolio.marketdata.client;


import com.realtimeportfolio.common.dto.StockTickerDto;
import com.realtimeportfolio.common.dto.TickersDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface RemoteStockClient {
    List<StockTickerDto> getTopStocks(TickersDto symbols) ;
    Optional<BigDecimal> getStockPrice(String tickerSymbol);
}
