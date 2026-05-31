package com.portfolio.service;

import org.portfolio.dto.StockResponse;

import java.util.Optional;

public interface StackService {
   Optional<StockResponse> getStockByTickerSymbol(String stockCode);
}
