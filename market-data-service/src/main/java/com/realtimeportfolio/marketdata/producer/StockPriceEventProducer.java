package com.realtimeportfolio.marketdata.producer;



import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.marketdata.client.RemoteStockClient;

import com.realtimeportfolio.common.config.kafka.KafkaTopics;
import com.realtimeportfolio.common.dto.StockPriceUpdateEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class StockPriceEventProducer {
    private final RemoteStockClient remoteStockClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public StockPriceEventProducer(
            RemoteStockClient remoteStockClient,
            KafkaTemplate<String, Object> kafkaTemplate
    ) {
        this.remoteStockClient = remoteStockClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void fetchAndPublishPrice(String tickerSymbol) {
        Optional<BigDecimal> priceOptional =
                remoteStockClient.getStock(tickerSymbol)
                        .map(dto-> dto.getCurrentPrice());

        if (priceOptional.isEmpty()) {
            log.warn("Current price not available for tickerSymbol={}", tickerSymbol);
            return;
        }

        StockPriceUpdateEvent event = new StockPriceUpdateEvent(
                tickerSymbol.toUpperCase(),
                priceOptional.get(),
                LocalDateTime.now()
        );

        kafkaTemplate.send(
                KafkaTopics.STOCK_PRICE_UPDATES,
                event.getTickerSymbol(),
                event
        );

        log.info("Stock price event published. tickerSymbol={}, price={}",
                event.getTickerSymbol(),
                event.getCurrentPrice()
        );
    }
}