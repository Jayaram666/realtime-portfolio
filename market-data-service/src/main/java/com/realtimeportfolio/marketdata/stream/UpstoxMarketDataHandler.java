package com.realtimeportfolio.marketdata.stream;

import com.realtimeportfolio.common.dto.StockPriceUpdateEvent;
import com.realtimeportfolio.common.util.StockSymbolTokenMap;
import com.realtimeportfolio.marketdata.producer.StackPriceUpdateProducer;
import com.upstox.feeder.MarketUpdateV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpstoxMarketDataHandler {

    private final StackPriceUpdateProducer stackPriceUpdateProducer;

    public void handleMarketUpdate(MarketUpdateV3 marketUpdate) {
        log.info("Invoking handleMarketUpdate");
        if (marketUpdate == null || marketUpdate.getFeeds() == null) return;

        Map<String, String> tokenToSymbolMap = StockSymbolTokenMap.getAllSymbols();

        marketUpdate.getFeeds().forEach((instrumentToken, feed) -> {
            String tickerSymbol = tokenToSymbolMap.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(instrumentToken))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);

            if (tickerSymbol == null || feed == null || feed.getFullFeed() == null) return;
            Double currentPrice = extractPrice(feed);
            if (currentPrice != null) {
                log.info("Ticker: {}, Current Price: {}", tickerSymbol, currentPrice);
                StockPriceUpdateEvent event = new StockPriceUpdateEvent(tickerSymbol, BigDecimal.valueOf(currentPrice), LocalDateTime.now());
                stackPriceUpdateProducer.produce(event);
            }
        });
    }

    private Double extractPrice(MarketUpdateV3.Feed feed) {
        if (feed.getFullFeed().getIndexFF() != null && feed.getFullFeed().getIndexFF().getLtpc() != null) {
            return feed.getFullFeed().getIndexFF().getLtpc().getLtp();
        }
        if (feed.getFullFeed().getMarketFF() != null && feed.getFullFeed().getMarketFF().getLtpc() != null) {
            return feed.getFullFeed().getMarketFF().getLtpc().getLtp();
        }
        return null;
    }
}
