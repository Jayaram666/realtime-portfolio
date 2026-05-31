package com.portfolio.service;

import org.portfolio.dto.StockTickerDto;

import java.util.List;

public interface StackMarketService {

    List<org.portfolio.dto.StockTickerDto> getStockTickers() ;
}