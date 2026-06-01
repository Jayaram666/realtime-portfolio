package com.realtimeportfolio.portfolio.service.impl;


import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.portfolio.client.StackClient;
import com.realtimeportfolio.portfolio.service.StackService;
import lombok.RequiredArgsConstructor;
import com.realtimeportfolio.common.dto.StockResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class StackServiceImpl implements StackService {

    private final StackClient stackClient;

    @Override
    public Optional<StockResponse> getStockByTickerSymbol(String tickerSymbol) {
        if (tickerSymbol == null || tickerSymbol.isBlank()) {
            log.warn("Skipping stock-service lookup because tickerSymbol is blank");
            return Optional.empty();
        }
        String normalizedTicker = tickerSymbol.trim().toUpperCase();
        log.debug("Calling stack-service for tickerSymbol={}", normalizedTicker);
        StockResponse response = stackClient.getStockByTickerSymbol(normalizedTicker);
        log.debug("stack-service lookup completed. tickerSymbol={}, found={}", normalizedTicker, response != null);
        return Optional.ofNullable(response);
    }
}
