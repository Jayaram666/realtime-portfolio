//package com.realtimeportfolio.marketdata.client;
//
//import com.realtimeportfolio.common.dto.StockTickerDto;
//import com.realtimeportfolio.common.dto.TickersDto;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Optional;
//
//import com.realtimeportfolio.common.util.StockSymbolTokenMap;
//import com.upstox.ApiClient;
//import com.upstox.ApiException;
//import com.upstox.Configuration;
//import com.upstox.api.*;
//import com.upstox.auth.OAuth;
//import io.swagger.client.api.MarketQuoteApi;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//public class UpStackClientImpl implements RemoteStockClient {
//
//    @Override
//    public List<StockTickerDto> getTopStocks(TickersDto symbols) {
//        return List.of();
//    }
//
//    @Override
//    public Optional<BigDecimal> getStockPrice(String tickerSymbol) {
//        return getStackBySymbol(tickerSymbol) != null ? Optional.of(getStackBySymbol(tickerSymbol).getCurrentPrice()) : Optional.empty();
//    }
//
//    @Override
//    public Optional<StockTickerDto> getStock(String tickerSymbol) {
//       log.info("Fetching stock data for tickerSymbol: {}", tickerSymbol);
//        return getStackBySymbol(tickerSymbol) != null ? Optional.of(getStackBySymbol(tickerSymbol)) : Optional.empty();
//    }
//
//
//    private StockTickerDto getStackBySymbol(String tickerSymbol) {
//
//        String instrumentToken = StockSymbolTokenMap.getValueByKey(tickerSymbol);
//        ApiClient defaultClient = Configuration.getDefaultApiClient();
////        String token = "eyJ0eXAiOiJKV1QiLCJrZXlfaWQiOiJza192MS4wIiwiYWxnIjoiSFMyNTYifQ.eyJzdWIiOiIyS0M5UDQiLCJqdGkiOiI2YTFkYTlhYTBmNmRhNDA4YzMxYmM0ZWYiLCJpc011bHRpQ2xpZW50IjpmYWxzZSwiaXNQbHVzUGxhbiI6ZmFsc2UsImlhdCI6MTc4MDMyODg3NCwiaXNzIjoidWRhcGktZ2F0ZXdheS1zZXJ2aWNlIiwiZXhwIjoxNzgwMzUxMjAwfQ.x49l9BJ-MXMa014HxqNar8ybt6l91CMmzysf-xFooJo";
//        String token = "eyJ0eXAiOiJKV1QiLCJrZXlfaWQiOiJza192MS4wIiwiYWxnIjoiSFMyNTYifQ.eyJzdWIiOiIyS0M5UDQiLCJqdGkiOiI2YTFlYmNjZGVlMGY0ODY3ODc5ODYwNDYiLCJpc011bHRpQ2xpZW50IjpmYWxzZSwiaXNQbHVzUGxhbiI6ZmFsc2UsImlhdCI6MTc4MDM5OTMwOSwiaXNzIjoidWRhcGktZ2F0ZXdheS1zZXJ2aWNlIiwiZXhwIjoxNzgwNDM3NjAwfQ._SXKm_dFszZPZxQNE4YWzbnEvF68rNWUdOZhYvoZG1k";
//
//        OAuth OAUTH2 = (OAuth) defaultClient.getAuthentication("OAUTH2");
//        OAUTH2.setAccessToken(token);
//
//        MarketQuoteApi apiInstance = new MarketQuoteApi();
//        ApiClient client = Configuration.getDefaultApiClient();
//        client.setAccessToken(token);
//        MarketQuoteApi apiInstance1 = new MarketQuoteApi(client);
//        apiInstance.setApiClient(defaultClient);
//        String apiVersion = "2.0";
//        StockTickerDto enrichedStockTickerDto = null;
//        try {
//            GetFullMarketQuoteResponse result = apiInstance.getFullMarketQuote(instrumentToken, apiVersion);
//
//            System.out.println("The entry set size is: " + result.getData().entrySet().size());
//           enrichedStockTickerDto = result.getData().entrySet()
//                    .stream()
//                    .filter(entry -> entry.getKey().contains(tickerSymbol))
//                    .map(entry -> {
//                        StockTickerDto stockTickerDto = new StockTickerDto();
//                        stockTickerDto.setSymbol(tickerSymbol);
//                        stockTickerDto.setCurrentPrice(BigDecimal.valueOf(entry.getValue().getLastPrice()));
//                        return stockTickerDto;
//                    }).findFirst().get();
//
////                    .forEach(entry -> {
////                String key = entry.getKey();
////                stockTickerDto.setSymbol();
////                stockTickerDto.setCurrentPrice(entry.getValue().getLastPrice());
////                System.out.println("Key: " + key);
////                System.out.println("Value: " + entry.getValue());
////            });
////
////            System.out.println(result);
////            Double lastprice = result.getData().get(symbol).getLastPrice();
////            System.out.println("Last Price: " + lastprice);
//        } catch (ApiException e) {
//            System.err.println("Exception when calling MarketQuoteApi#getFullMarketQuote");
//            e.printStackTrace();
//        }
//        log.info("Fetched stock data for tickerSymbol: {}. currentPrice={}", tickerSymbol, enrichedStockTickerDto != null ? enrichedStockTickerDto.getCurrentPrice() : "N/A");
//        return enrichedStockTickerDto;
//    }
//
//    /*private static void getStackData(String[] tickerSymbols) {
//
//        String symbol = String.join(",", tickerSymbols);
//        ApiClient defaultClient = Configuration.getDefaultApiClient();
//        String token = "eyJ0eXAiOiJKV1QiLCJrZXlfaWQiOiJza192MS4wIiwiYWxnIjoiSFMyNTYifQ.eyJzdWIiOiIyS0M5UDQiLCJqdGkiOiI2YTFkYTlhYTBmNmRhNDA4YzMxYmM0ZWYiLCJpc011bHRpQ2xpZW50IjpmYWxzZSwiaXNQbHVzUGxhbiI6ZmFsc2UsImlhdCI6MTc4MDMyODg3NCwiaXNzIjoidWRhcGktZ2F0ZXdheS1zZXJ2aWNlIiwiZXhwIjoxNzgwMzUxMjAwfQ.x49l9BJ-MXMa014HxqNar8ybt6l91CMmzysf-xFooJo";
//        OAuth OAUTH2 = (OAuth) defaultClient.getAuthentication("OAUTH2");
//        OAUTH2.setAccessToken(token);
//
//        MarketQuoteApi apiInstance = new MarketQuoteApi();
//        ApiClient client = Configuration.getDefaultApiClient();
//        client.setAccessToken(token);
//        MarketQuoteApi apiInstance1 = new MarketQuoteApi(client);
//        apiInstance.setApiClient(defaultClient);
////        String symbol = "NSE_EQ|INE009A01021,NSE_FO|99341,NSE_FO|144145,NSE_EQ|INE883A01011,NSE_FO|144742,NSE_EQ|INE002A01018,NSE_FO|101228";
//        String apiVersion = "2.0";
//
//        try {
//            GetFullMarketQuoteResponse result = apiInstance.getFullMarketQuote(symbol, apiVersion);
//
//            System.out.println("The entry set size is: " + result.getData().entrySet().size());
//            result.getData().entrySet()
//                    .forEach(entry -> {
//                        String key = entry.getKey();
//                        System.out.println("Key: " + key);
//                        System.out.println("Value: " + entry.getValue());
//                    });
//
//            System.out.println(result);
//            Double lastprice = result.getData().get(symbol).getLastPrice();
//            System.out.println("Last Price: " + lastprice);
//        } catch (ApiException e) {
//            System.err.println("Exception when calling MarketQuoteApi#getFullMarketQuote");
//            e.printStackTrace();
//        }
//    }*/
//}
//
//
