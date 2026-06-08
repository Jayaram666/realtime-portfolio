package com.realtimeportfolio.stock.service;

import com.realtimeportfolio.stock.dto.*;

import java.util.List;

public interface StockService {

     List<StockResponse> getAllActiveStocks();

     StockResponse getStockByTickerSymbol(String tickerSymbol);

    StockResponse createStock(CreateStockRequest request);


    StockValidationResponse validateTickerSymbol(String tickerSymbol);

    StockResponse updateStock(Long id, UpdateStockRequest request);

    StockResponse updateStockStatus(Long id, StockStatusUpdateRequest request);
}