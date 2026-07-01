package com.realtimeportfolio.marketdata.quote;

import com.realtimeportfolio.common.dto.StockTickerDto;
import com.realtimeportfolio.common.dto.TickersDto;
import com.realtimeportfolio.common.util.StockSymbolTokenMap;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpstoxClientImpl implements RemoteStockClient {

    private static final String STOCK_PROVIDER_RETRY = "stockProviderRetry";
    private static final String STOCK_PROVIDER_CIRCUIT_BREAKER = "stockProviderCircuitBreaker";

    private final UpstoxMarketQuoteService upstoxMarketQuoteService;

    @Override
    @Retry(name = STOCK_PROVIDER_RETRY, fallbackMethod = "fallbackTopStocks")
    @CircuitBreaker(name = STOCK_PROVIDER_CIRCUIT_BREAKER, fallbackMethod = "fallbackTopStocks")
    public List<StockTickerDto> getTopStocks() {
        log.info("Fetching top stocks from stock provider.");
        return getStocks(StockSymbolTokenMap.getAllSymbols().keySet().toArray(new String[0]));
    }

    @Retry(name = STOCK_PROVIDER_RETRY, fallbackMethod = "fallbackStocks")
    @CircuitBreaker(name = STOCK_PROVIDER_CIRCUIT_BREAKER, fallbackMethod = "fallbackStocks")
    public List<StockTickerDto> getStocks(String[] normalizedTickerSymbols) {
        log.info("Fetching multiple stock quotes. tickerSymbols={}", normalizedTickerSymbols);
        return upstoxMarketQuoteService.fetchStockQuotes(normalizedTickerSymbols);
    }

    @Override
    @Retry(name = STOCK_PROVIDER_RETRY, fallbackMethod = "fallbackStockPrice")
    @CircuitBreaker(name = STOCK_PROVIDER_CIRCUIT_BREAKER, fallbackMethod = "fallbackStockPrice")
    public Optional<BigDecimal> getStockPrice(String tickerSymbol) {
        return getStock(tickerSymbol)
                .map(StockTickerDto::getCurrentPrice);
    }

    @Override
    @Retry(name = STOCK_PROVIDER_RETRY, fallbackMethod = "fallbackStockTicker")
    @CircuitBreaker(name = STOCK_PROVIDER_CIRCUIT_BREAKER, fallbackMethod = "fallbackStockTicker")
    public Optional<StockTickerDto> getStock(String tickerSymbol) {
        if (tickerSymbol == null || tickerSymbol.isBlank()) {
            log.warn("Cannot fetch stock because tickerSymbol is blank.");
            return Optional.empty();
        }

        String normalizedTickerSymbol = tickerSymbol.trim().toUpperCase();

        log.info("Fetching stock data from stock provider. tickerSymbol={}", normalizedTickerSymbol);

        String instrumentToken = StockSymbolTokenMap.getValueByKey(normalizedTickerSymbol);

        if (instrumentToken == null || instrumentToken.isBlank()) {
            log.warn(
                    "Instrument token not found for tickerSymbol={}. Please check StockSymbolTokenMap.",
                    normalizedTickerSymbol
            );
            return Optional.empty();
        }

        return upstoxMarketQuoteService.fetchStockQuote(normalizedTickerSymbol, instrumentToken);
    }

    private Optional<StockTickerDto> fallbackStockTicker(String tickerSymbol, Throwable ex) {
        log.error(
                "Stock provider fallback triggered while fetching stock. tickerSymbol={}, reason={}",
                tickerSymbol,
                ex.getMessage(),
                ex
        );

        return Optional.empty();
    }

    private Optional<BigDecimal> fallbackStockPrice(String tickerSymbol, Throwable ex) {
        log.error(
                "Stock provider fallback triggered while fetching stock price. tickerSymbol={}, reason={}",
                tickerSymbol,
                ex.getMessage(),
                ex
        );

        return Optional.empty();
    }

    private List<StockTickerDto> fallbackTopStocks(Throwable ex) {
        log.error(
                "Stock provider fallback triggered while fetching top stocks");

        return List.of();
    }
}