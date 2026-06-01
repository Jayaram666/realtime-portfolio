package com.realtimeportfolio.marketdata.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.common.dto.StockTickerDto;
import com.realtimeportfolio.common.dto.TickersDto;
import com.realtimeportfolio.common.exception.YahooFinanceClientException;
import org.springframework.stereotype.Component;
import yahoofinance.Stock;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Component
public class YahooFinanceStockMarketClient implements RemoteStockClient {

    private static final String CURRENCY = "INR";

    @Override
    @Retry(name = "yahooFinanceRetry", fallbackMethod = "fallbackStockTicker")
    @CircuitBreaker(name = "yahooFinanceCircuitBreaker", fallbackMethod = "fallbackStockTicker")
    public List<StockTickerDto> getTopStocks(TickersDto tickersDto) {
        try {
            return getStockDataFromYahoo(tickersDto.getTickers());
        } catch (IOException e) {
            throw new YahooFinanceClientException("Failed to fetch stock data from Yahoo Finance", e);
        }
    }

    @Override
    public Optional<BigDecimal> getStockPrice(String tickerSymbol) {
        try {
            return getStockDataFromYahoo(new String[]{tickerSymbol})
                    .stream()
                    .filter(ticker -> ticker.getSymbol().equalsIgnoreCase(tickerSymbol))
                    .findFirst()
                    .map(StockTickerDto::getCurrentPrice);
        } catch (IOException e) {
            throw new YahooFinanceClientException("Failed to fetch stock data from Yahoo Finance", e);
        }
    }

    private static List<StockTickerDto> getStockDataFromYahoo(String[] symbols) throws IOException {
        List<StockTickerDto> stockTickers = new ArrayList<>();
        log.info("Fetching stock data for symbols: {}", String.join(", ", symbols));
        Map<String, Stock> stocks = yahoofinance.YahooFinance.get(symbols);

        for (String symbol : symbols) {
            Stock stock = stocks.get(symbol);
            if (stock == null || stock.getQuote() == null) {
                log.warn("Stock data not available for symbol={}", symbol);
                continue;
            }

            BigDecimal price = stock.getQuote().getPrice();
            BigDecimal previousClose = stock.getQuote().getPreviousClose();
            BigDecimal change = null;

            if (price != null && previousClose != null) {
                change = price.subtract(previousClose);
            }
            stockTickers.add(new StockTickerDto(
                    symbol,
                    price,
                    previousClose,
                    change,
                    CURRENCY
            ));
        }
        return stockTickers;
    }

    public List<StockTickerDto> fallbackStockTicker(Exception ex) {
        log.error("Yahoo Finance fallback executed. Returning empty stock ticker list", ex);
        return Collections.emptyList();
    }
}
