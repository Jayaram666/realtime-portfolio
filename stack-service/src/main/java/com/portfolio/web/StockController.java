package com.portfolio.web;



import com.portfolio.dto.*;
import com.portfolio.service.StockService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    /**
     * Admin API.
     * Used to add new stock master data.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StockResponse createStock(
            @Valid @RequestBody CreateStockRequest request
    ) {
        return stockService.createStock(request);
    }

    /**
     * Used by frontend dropdown.
     * Returns only active stocks.
     */
    @GetMapping
    public List<StockResponse> getAllActiveStocks() {
        return stockService.getAllActiveStocks();
    }

    /**
     * Used for autocomplete/search.
     *
     * Example:
     * GET /api/stocks/search?keyword=tcs
     * GET /api/stocks/search?keyword=tat
     */
    @GetMapping("/search")
    public List<StockResponse> searchStocks(
            @RequestParam String keyword
    ) {
        return stockService.searchStocks(keyword);
    }

    /**
     * Used by portfolio-service to fetch stock details.
     *
     * Example:
     * GET /api/stocks/TCS
     */
    @GetMapping("/{tickerSymbol}")
    public StockResponse getStockByTickerSymbol(
            @PathVariable String tickerSymbol
    ) {
        return stockService.getStockByTickerSymbol(tickerSymbol);
    }

    /**
     * Used by portfolio-service to validate ticker.
     *
     * Example:
     * GET /api/stocks/TCS/valid
     */
    @GetMapping("/{tickerSymbol}/valid")
    public StockValidationResponse validateTickerSymbol(
            @PathVariable String tickerSymbol
    ) {
        return stockService.validateTickerSymbol(tickerSymbol);
    }

    /**
     * Admin API.
     * Used to update stock master details.
     */
    @PutMapping("/{id}")
    public StockResponse updateStock(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStockRequest request
    ) {
        return stockService.updateStock(id, request);
    }

    /**
     * Admin API.
     * Used to activate/deactivate stock.
     */
    @PatchMapping("/{id}/status")
    public StockResponse updateStockStatus(
            @PathVariable Long id,
            @Valid @RequestBody StockStatusUpdateRequest request
    ) {
        return stockService.updateStockStatus(id, request);
    }
}