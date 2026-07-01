package com.realtimeportfolio.stock.web;


import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.stock.dto.*;
import com.realtimeportfolio.stock.service.StockService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StockResponse createStock(
            @Valid @RequestBody CreateStockRequest request
    ) {
        log.info("Create stock API called. tickerSymbol={}, exchange={}", request.getTickerSymbol(), request.getExchange());
        StockResponse response = stockService.createStock(request);
        log.info("Create stock API completed. stockId={}, tickerSymbol={}", response.getId(), response.getTickerSymbol());
        return response;
    }

    @GetMapping
    public List<StockResponse> getAllActiveStocks() {
        log.info("Get all active stocks API called");
        List<StockResponse> response = stockService.getAllActiveStocks();
        log.info("Get all active stocks API completed. count={}", response.size());
        return response;
    }

    @GetMapping("/{tickerSymbol}")
    public StockResponse getStockByTickerSymbol(
            @PathVariable("tickerSymbol") String tickerSymbol
    ) {
        log.info("Get stock by ticker API called. tickerSymbol={}", tickerSymbol);
        StockResponse response = stockService.getStockByTickerSymbol(tickerSymbol);
        log.info("Get stock by ticker API completed. stockId={}, tickerSymbol={}", response.getId(), response.getTickerSymbol());
        return response;
    }

    @GetMapping("/{tickerSymbol}/valid")
    public StockValidationResponse validateTickerSymbol(
            @PathVariable("tickerSymbol") String tickerSymbol
    ) {
        log.info("Validate ticker API called. tickerSymbol={}", tickerSymbol);
        StockValidationResponse response = stockService.validateTickerSymbol(tickerSymbol);
        log.info("Validate ticker API completed. tickerSymbol={}, valid={}", response.getTickerSymbol(), response.isValid());
        return response;
    }


    @PutMapping("/{id}")
    public StockResponse updateStock(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateStockRequest request
    ) {
        log.info("Update stock API called. stockId={}, tickerSymbol={}, exchange={}", id, request.getTickerSymbol(), request.getExchange());
        StockResponse response = stockService.updateStock(id, request);
        log.info("Update stock API completed. stockId={}, tickerSymbol={}", response.getId(), response.getTickerSymbol());
        return response;
    }

}