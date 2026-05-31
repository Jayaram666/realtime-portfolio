package com.portfolio.client;


import org.portfolio.dto.StockTickerDto;
import org.portfolio.dto.TickersDto;

import java.util.List;

public interface StockClient {
    List<StockTickerDto> getTopStocks(TickersDto symbols) ;
}
