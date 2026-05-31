package com.portfolio.producer;


import com.portfolio.client.RemoteStockClient;

import com.portfolio.kafka.KafkaTopics;
import org.portfolio.dto.StockPriceUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class StockPriceEventProducer {

    private static final Logger log =
            LoggerFactory.getLogger(StockPriceEventProducer.class);

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
                remoteStockClient.getStockPrice(tickerSymbol);

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