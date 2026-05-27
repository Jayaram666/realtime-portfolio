package com.portfolio.client;

import com.portfolio.dto.StockTickerDto;

import java.io.IOException;
import java.util.List;

public interface StockClient {
    List<StockTickerDto> getTopStocks(String[] symbols) ;
}
