package com.realtimeportfolio.portfolio.service;

import com.realtimeportfolio.common.dto.StockResponse;

import java.util.Optional;

public interface StackService {
   Optional<StockResponse> getStockByTickerSymbol(String stockCode);
}
