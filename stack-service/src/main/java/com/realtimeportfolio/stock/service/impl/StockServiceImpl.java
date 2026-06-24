package com.realtimeportfolio.stock.service.impl;



import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.stock.dto.*;
import com.realtimeportfolio.stock.entity.Stock;
import com.realtimeportfolio.stock.repo.StockRepository;
import com.realtimeportfolio.stock.service.StockService;

import com.realtimeportfolio.common.exception.BusinessException;
import com.realtimeportfolio.common.exception.DuplicateResourceException;
import com.realtimeportfolio.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;

    public StockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    @Transactional
    public StockResponse createStock(CreateStockRequest request) {

        String tickerSymbol = normalizeTicker(request.getTickerSymbol());
        String exchange = normalizeExchange(request.getExchange());

        log.info("Create stock request received. tickerSymbol={}, exchange={}",
                tickerSymbol,
                exchange
        );

        boolean alreadyExists =
                stockRepository.existsByTickerSymbolIgnoreCase(
                        tickerSymbol
                );

        if (alreadyExists) {
            throw new DuplicateResourceException(
                    "Stock already exists with ticker symbol: " + tickerSymbol + " and exchange: " + exchange
            );
        }

        Stock stock = new Stock();
        stock.setCompanyName(request.getCompanyName().trim());
        stock.setTickerSymbol(tickerSymbol);
        stock.setExchange(exchange);
        stock.setActive(true);

        Stock savedStock = stockRepository.save(stock);

        log.info("Stock created successfully. stockId={}, tickerSymbol={}",
                savedStock.getId(),
                savedStock.getTickerSymbol()
        );

        return toResponse(savedStock);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockResponse> getAllActiveStocks() {

        log.info("Fetching all active stocks");

        return stockRepository.findByActiveTrueOrderByCompanyNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public StockResponse getStockByTickerSymbol(String tickerSymbol) {

        String normalizedTicker = normalizeTicker(tickerSymbol);

        log.info("Fetching stock by tickerSymbol={}", normalizedTicker);

        Stock stock = stockRepository
                .findByTickerSymbolIgnoreCaseAndActiveTrue(normalizedTicker)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Active stock not found with ticker symbol: " + normalizedTicker
                ));

        return toResponse(stock);
    }

    @Override
    @Transactional(readOnly = true)
    public StockValidationResponse validateTickerSymbol(String tickerSymbol) {

        String normalizedTicker = normalizeTicker(tickerSymbol);

        log.info("Validating tickerSymbol={}", normalizedTicker);

        boolean valid =
                stockRepository.existsByTickerSymbolIgnoreCaseAndActiveTrue(normalizedTicker);

        return new StockValidationResponse(normalizedTicker, valid);
    }

    @Override
    @Transactional
    public StockResponse updateStock(Long id, UpdateStockRequest request) {

        log.info("Update stock request received. stockId={}", id);

        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Stock not found with id: " + id
                ));

        String newTickerSymbol = normalizeTicker(request.getTickerSymbol());
        String newExchange = normalizeExchange(request.getExchange());

        boolean duplicateExists =
                stockRepository.existsByTickerSymbolIgnoreCaseAndExchangeIgnoreCaseAndIdNot(
                        newTickerSymbol,
                        newExchange,
                        id
                );

        if (duplicateExists) {
            throw new DuplicateResourceException(
                    "Another stock already exists with ticker symbol: "
                            + newTickerSymbol + " and exchange: " + newExchange
            );
        }

        stock.setCompanyName(request.getCompanyName().trim());
        stock.setTickerSymbol(newTickerSymbol);
        stock.setExchange(newExchange);
        stock.setActive(request.getActive());

        Stock updatedStock = stockRepository.save(stock);

        log.info("Stock updated successfully. stockId={}, tickerSymbol={}",
                updatedStock.getId(),
                updatedStock.getTickerSymbol()
        );

        return toResponse(updatedStock);
    }

    @Override
    @Transactional
    public StockResponse updateStockStatus(Long id, StockStatusUpdateRequest request) {

        log.info("Update stock status request received. stockId={}, active={}",
                id,
                request.getActive()
        );

        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Stock not found with id: " + id
                ));

        stock.setActive(request.getActive());

        Stock updatedStock = stockRepository.save(stock);

        log.info("Stock status updated successfully. stockId={}, active={}",
                updatedStock.getId(),
                updatedStock.isActive()
        );

        return toResponse(updatedStock);
    }

    private StockResponse toResponse(Stock stock) {
        return new StockResponse(
                stock.getId(),
                stock.getCompanyName(),
                stock.getTickerSymbol(),
                stock.isActive()

        );
    }

    private String normalizeTicker(String tickerSymbol) {
        if (tickerSymbol == null || tickerSymbol.isBlank()) {
            throw new BusinessException("Ticker symbol is required");
        }

        return tickerSymbol.trim().toUpperCase();
    }

    private String normalizeExchange(String exchange) {
        if (exchange == null || exchange.isBlank()) {
            throw new BusinessException("Exchange is required");
        }

        return exchange.trim().toUpperCase();
    }
}