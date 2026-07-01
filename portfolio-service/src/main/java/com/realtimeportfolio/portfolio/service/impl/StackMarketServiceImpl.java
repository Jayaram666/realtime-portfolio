package com.realtimeportfolio.portfolio.service.impl;


import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.portfolio.client.StackMarketClient;
import com.realtimeportfolio.portfolio.service.StackMarketService;
import lombok.RequiredArgsConstructor;
import com.realtimeportfolio.common.dto.StockTickerDto;
import com.realtimeportfolio.common.dto.TickersDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StackMarketServiceImpl implements StackMarketService {

    private final StackMarketClient stackMarketClient;

    @Value("${portfolio.market.default-tickers:TCS,INFY,RELIANCE,HDFCBANK}")
    private String defaultTickers;

    @Override
    public List<StockTickerDto> getStockTickers() {
        log.debug("Calling market-data-service for default tickers. configuredTickers={}", defaultTickers);
        List<StockTickerDto> response = stackMarketClient.getStockTickers().getData();
        log.debug("market-data-service ticker call completed. resultCount={}", response.size());
        return response;
    }

    @Override
    public Optional<StockTickerDto> getStockPrice(String tickerSymbol) {
        if (tickerSymbol == null || tickerSymbol.isBlank()) {
            return Optional.empty();
        }
        String normalizedTicker = tickerSymbol.trim().toUpperCase();
        log.debug("Calling market-data-service for current price. tickerSymbol={}", normalizedTicker);
        BigDecimal currentPrice = stackMarketClient.getCurrentPriceBySymbol(normalizedTicker)
                .getData().getPrice();
        log.debug("market-data-service price call completed. tickerSymbol={}, found={}", normalizedTicker, currentPrice != null);
        return Optional.ofNullable(currentPrice)
                .map(price -> new StockTickerDto(normalizedTicker, price, null, null, "INR"));
    }
}
