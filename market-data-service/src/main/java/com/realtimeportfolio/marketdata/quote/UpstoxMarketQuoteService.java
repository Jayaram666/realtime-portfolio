package com.realtimeportfolio.marketdata.quote;

import com.realtimeportfolio.common.dto.StockTickerDto;
import com.realtimeportfolio.common.util.StockSymbolTokenMap;
import com.realtimeportfolio.marketdata.config.UpstoxProperties;
import com.upstox.ApiException;
import com.upstox.api.GetFullMarketQuoteResponse;
import com.upstox.api.MarketQuoteSymbol;
import io.swagger.client.api.MarketQuoteApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpstoxMarketQuoteService {

    private final UpstoxProperties upstoxProperties;
    private final MarketQuoteApi marketQuoteApi;

    public Optional<StockTickerDto> fetchStockQuote(String tickerSymbol, String instrumentToken) {
        log.info("Fetching stock quote from Upstox. tickerSymbol={}, instrumentToken={}", tickerSymbol, instrumentToken);

        try {
            GetFullMarketQuoteResponse response = marketQuoteApi.getFullMarketQuote(instrumentToken, upstoxProperties.getApiVersion());

            if (response == null || response.getData() == null || response.getData().isEmpty()) {
                log.warn("No market quote data received from Upstox. tickerSymbol={}, instrumentToken={}", tickerSymbol, instrumentToken);
                return Optional.empty();
            }

            String key =null;
            AtomicReference<Double> lastprice = new AtomicReference<>();
            // Upstox map key is the instrument token
            response.getData().forEach((k,v)->{
                System.out.println("Keye :"+k);
                System.out.println("Value :"+v.getLastPrice());
                lastprice.set(v.getLastPrice());
                System.out.println("Other data :"+v.getInstrumentToken());
            });
//            MarketQuoteSymbol marketQuote = response.getData().get(instrumentToken);
//            if (marketQuote == null || marketQuote.getLastPrice() == null) {
//                log.warn("Market quote or last price is missing in Upstox response. tickerSymbol={}, instrumentToken={}", tickerSymbol, instrumentToken);
//                return Optional.empty();
//            }

            StockTickerDto stockTickerDto = new StockTickerDto();
            stockTickerDto.setSymbol(tickerSymbol);
            stockTickerDto.setCurrentPrice(BigDecimal.valueOf(lastprice.get()));

            log.info("Stock quote fetched successfully. tickerSymbol={}, price={}", tickerSymbol, stockTickerDto.getCurrentPrice());
            return Optional.of(stockTickerDto);

        } catch (ApiException ex) {
            log.error("Upstox API call failed. tickerSymbol={}, instrumentToken={}, statusCode={}, responseBody={}",
                    tickerSymbol, instrumentToken, ex.getCode(), ex.getResponseBody(), ex);
            return Optional.empty();
        } catch (Exception ex) {
            log.error("Unexpected error while fetching stock quote from Upstox. tickerSymbol={}, instrumentToken={}", tickerSymbol, instrumentToken, ex);
            return Optional.empty();
        }
    }

    public List<StockTickerDto> fetchStockQuotes(String[] tickerSymbols) {
        if (tickerSymbols == null || tickerSymbols.length == 0) {
            log.warn("Cannot fetch stock quotes because tickerSymbols input is empty.");
            return List.of();
        }

        Map<String, String> tickerToInstrumentTokenMap = StockSymbolTokenMap.getAllSymbols();
        if (tickerToInstrumentTokenMap.isEmpty()) {
            log.warn("No valid instrument tokens found for requested tickerSymbols={}", Arrays.toString(tickerSymbols));
            return List.of();
        }

        String instrumentTokens = String.join(",", tickerToInstrumentTokenMap.values());

        log.info("Fetching bulk stock quotes from Upstox. requestedTickerCount={}, validTickerCount={}, instrumentTokens={}",
                tickerSymbols.length, tickerToInstrumentTokenMap.size(), instrumentTokens);

        try {
            var response = marketQuoteApi.getFullMarketQuote(instrumentTokens, upstoxProperties.getApiVersion());

            if (response == null || response.getData() == null || response.getData().isEmpty()) {
                log.warn("No stock quote data received from Upstox. tickerSymbols={}, instrumentTokens={}", Arrays.toString(tickerSymbols), instrumentTokens);
                return List.of();
            }

            // Optimization: Create an inverted lookup map (Token -> Symbol) upfront for O(1) lightning fast lookups
            Map<String, String> tokenToTickerMap = tickerToInstrumentTokenMap.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (existing, replacement) -> existing));

            List<StockTickerDto> stockTickers = response.getData()
                    .entrySet()
                    .stream()
                    .map(entry -> mapToStockTickerDto(entry.getKey(), entry.getValue(), tokenToTickerMap))
                    .flatMap(Optional::stream)
                    .toList();

            log.info("Fetched stock quotes successfully from Upstox. requestedTickerCount={}, responseCount={}",
                    tickerToInstrumentTokenMap.size(), stockTickers.size());

            return stockTickers;

        } catch (ApiException ex) {
            log.error("Upstox API call failed while fetching stock quotes. tickerSymbols={}, instrumentTokens={}, statusCode={}, responseBody={}",
                    Arrays.toString(tickerSymbols), instrumentTokens, ex.getCode(), ex.getResponseBody(), ex);
            return List.of();
        } catch (Exception ex) {
            log.error("Unexpected error while fetching stock quotes from Upstox. tickerSymbols={}, instrumentTokens={}", Arrays.toString(tickerSymbols), instrumentTokens, ex);
            return List.of();
        }
    }

    private Map<String, String> buildTickerToInstrumentTokenMap(String[] tickerSymbols) {
        return Arrays.stream(tickerSymbols)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(ticker -> !ticker.isBlank())
                .map(String::toUpperCase)
                .distinct()
                .map(tickerSymbol -> {
                    String instrumentToken = StockSymbolTokenMap.getValueByKey(tickerSymbol);
                    if (instrumentToken == null || instrumentToken.isBlank()) {
                        log.warn("Instrument token not found for tickerSymbol={}", tickerSymbol);
                        return null;
                    }
                    return Map.entry(tickerSymbol, instrumentToken);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (existing, replacement) -> existing));
    }

    private Optional<StockTickerDto> mapToStockTickerDto(
            String responseKey,
            MarketQuoteSymbol marketQuote, // Fixed: Swapped Object with the explicit SDK concrete model type
            Map<String, String> tokenToTickerMap
    ) {

        System.out.println("tokenTickerMap"+tokenToTickerMap);
        try {
            String tickerSymbol = tokenToTickerMap.entrySet()
                    .stream()
                    .filter((entry)-> responseKey.contains(entry.getValue()))
                    .map(Map.Entry::getValue)
                    .findAny().get();

            if (tickerSymbol == null) {
                log.warn("Unable to map Upstox response token key to a local tickerSymbol. responseKey={}", responseKey);
                return Optional.empty();
            }

            if (marketQuote == null || marketQuote.getLastPrice() == null) {
                log.warn("MarketQuote or last price payload missing for responseKey={}", responseKey);
                return Optional.empty();
            }

            StockTickerDto dto = new StockTickerDto();
            dto.setSymbol(tickerSymbol);
            dto.setCurrentPrice(BigDecimal.valueOf(marketQuote.getLastPrice()));

            return Optional.of(dto);
        } catch (Exception ex) {
            log.error("Error mapping market update to DTO for responseKey={}", responseKey, ex);
            return Optional.empty();
        }
    }
}
