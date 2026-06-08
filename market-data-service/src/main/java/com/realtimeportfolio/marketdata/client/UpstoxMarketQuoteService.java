package com.realtimeportfolio.marketdata.client;


import com.realtimeportfolio.common.dto.StockTickerDto;
import com.realtimeportfolio.common.util.StockSymbolTokenMap;
import com.realtimeportfolio.marketdata.config.UpstoxProperties;
import com.upstox.ApiClient;
import com.upstox.ApiException;
import io.swagger.client.api.MarketQuoteApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpstoxMarketQuoteService {

    private final UpstoxProperties upstoxProperties;

    public Optional<StockTickerDto> fetchStockQuote(String tickerSymbol, String instrumentToken) {
        log.info(
                "Fetching stock quote from Upstox. tickerSymbol={}, instrumentToken={}",
                tickerSymbol,
                instrumentToken
        );

        try {
            MarketQuoteApi marketQuoteApi = createMarketQuoteApi();

            var response = marketQuoteApi.getFullMarketQuote(
                    instrumentToken,
                    upstoxProperties.getApiVersion()
            );

            if (response == null || response.getData() == null || response.getData().isEmpty()) {
                log.warn(
                        "No market quote data received from Upstox. tickerSymbol={}, instrumentToken={}",
                        tickerSymbol,
                        instrumentToken
                );
                return Optional.empty();
            }

            var quoteEntry = response.getData()
                    .entrySet()
                    .stream()
                    .findFirst();

            if (quoteEntry.isEmpty()) {
                log.warn(
                        "Market quote response data is empty. tickerSymbol={}, instrumentToken={}",
                        tickerSymbol,
                        instrumentToken
                );
                return Optional.empty();
            }

            Double lastPrice = quoteEntry.get().getValue().getLastPrice();

            if (lastPrice == null) {
                log.warn(
                        "Last price is missing in Upstox response. tickerSymbol={}, instrumentToken={}, responseKey={}",
                        tickerSymbol,
                        instrumentToken,
                        quoteEntry.get().getKey()
                );
                return Optional.empty();
            }

            StockTickerDto stockTickerDto = new StockTickerDto();
            stockTickerDto.setSymbol(tickerSymbol);
            stockTickerDto.setCurrentPrice(BigDecimal.valueOf(lastPrice));

            log.info(
                    "Stock quote fetched successfully. tickerSymbol={}, price={}, responseKey={}",
                    tickerSymbol,
                    stockTickerDto.getCurrentPrice(),
                    quoteEntry.get().getKey()
            );

            return Optional.of(stockTickerDto);

        } catch (ApiException ex) {
            log.error(
                    "Upstox API call failed. tickerSymbol={}, instrumentToken={}, statusCode={}, responseBody={}",
                    tickerSymbol,
                    instrumentToken,
                    ex.getCode(),
                    ex.getResponseBody(),
                    ex
            );
            return Optional.empty();

        } catch (Exception ex) {
            log.error(
                    "Unexpected error while fetching stock quote from Upstox. tickerSymbol={}, instrumentToken={}",
                    tickerSymbol,
                    instrumentToken,
                    ex
            );
            return Optional.empty();
        }
    }

    public List<StockTickerDto> fetchStockQuotes(String[] tickerSymbols) {
        if (tickerSymbols == null || tickerSymbols.length == 0) {
            log.warn("Cannot fetch stock quotes because tickerSymbols input is empty.");
            return List.of();
        }

        Map<String, String> tickerToInstrumentTokenMap = buildTickerToInstrumentTokenMap(tickerSymbols);

        if (tickerToInstrumentTokenMap.isEmpty()) {
            log.warn("No valid instrument tokens found for requested tickerSymbols={}", Arrays.toString(tickerSymbols));
            return List.of();
        }

        String instrumentTokens = String.join(",", tickerToInstrumentTokenMap.values());

        log.info(
                "Fetching stock quotes from Upstox. requestedTickerCount={}, validTickerCount={}, instrumentTokens={}",
                tickerSymbols.length,
                tickerToInstrumentTokenMap.size(),
                instrumentTokens
        );

        try {
            MarketQuoteApi marketQuoteApi = createMarketQuoteApi();

            var response = marketQuoteApi.getFullMarketQuote(
                    instrumentTokens,
                    upstoxProperties.getApiVersion()
            );

            if (response == null || response.getData() == null || response.getData().isEmpty()) {
                log.warn(
                        "No stock quote data received from Upstox. tickerSymbols={}, instrumentTokens={}",
                        Arrays.toString(tickerSymbols),
                        instrumentTokens
                );
                return List.of();
            }

            List<StockTickerDto> stockTickers = response.getData()
                    .entrySet()
                    .stream()
                    .map(entry -> mapToStockTickerDto(entry.getKey(), entry.getValue(), tickerToInstrumentTokenMap))
                    .flatMap(Optional::stream)
                    .toList();

            log.info(
                    "Fetched stock quotes successfully from Upstox. requestedTickerCount={}, responseCount={}",
                    tickerToInstrumentTokenMap.size(),
                    stockTickers.size()
            );

            return stockTickers;

        } catch (ApiException ex) {
            log.error(
                    "Upstox API call failed while fetching stock quotes. tickerSymbols={}, instrumentTokens={}, statusCode={}, responseBody={}",
                    Arrays.toString(tickerSymbols),
                    instrumentTokens,
                    ex.getCode(),
                    ex.getResponseBody(),
                    ex
            );
            return List.of();

        } catch (Exception ex) {
            log.error(
                    "Unexpected error while fetching stock quotes from Upstox. tickerSymbols={}, instrumentTokens={}",
                    Arrays.toString(tickerSymbols),
                    instrumentTokens,
                    ex
            );
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
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    private Optional<StockTickerDto> mapToStockTickerDto(
            String responseKey,
            Object quote,
            Map<String, String> tickerToInstrumentTokenMap
    ) {
        try {
            String tickerSymbol = findTickerSymbolByResponseKey(responseKey, tickerToInstrumentTokenMap);

            if (tickerSymbol == null) {
                log.warn("Unable to map Upstox response key to tickerSymbol. responseKey={}", responseKey);
                return Optional.empty();
            }

            /*
             * Replace this cast/type with your actual Upstox quote class if your IDE shows a concrete type.
             * In many generated Upstox SDKs, entry.getValue() has getLastPrice().
             */
            var quoteValue = (com.upstox.api.MarketQuoteSymbol) quote;

            Double lastPrice = quoteValue.getLastPrice();

            if (lastPrice == null) {
                log.warn("Last price is missing in Upstox response. tickerSymbol={}, responseKey={}", tickerSymbol, responseKey);
                return Optional.empty();
            }

            StockTickerDto stockTickerDto = new StockTickerDto();
            stockTickerDto.setSymbol(tickerSymbol);
            stockTickerDto.setCurrentPrice(BigDecimal.valueOf(lastPrice));

            return Optional.of(stockTickerDto);

        } catch (Exception ex) {
            log.error("Failed to map Upstox quote response. responseKey={}", responseKey, ex);
            return Optional.empty();
        }
    }

    private String findTickerSymbolByResponseKey(
            String responseKey,
            Map<String, String> tickerToInstrumentTokenMap
    ) {
        return tickerToInstrumentTokenMap.entrySet()
                .stream()
                .filter(entry -> responseKey.contains(entry.getKey()) || responseKey.contains(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
    private MarketQuoteApi createMarketQuoteApi() {
        ApiClient apiClient = new ApiClient();
        apiClient.setAccessToken(upstoxProperties.getAccessToken());
        return new MarketQuoteApi(apiClient);
    }
}