package com.portfolio.service;

import com.portfolio.dto.StockTickerDto;

import java.io.IOException;
import java.util.List;

public interface StockService {

    List<StockTickerDto> getStockTickers() ;
}