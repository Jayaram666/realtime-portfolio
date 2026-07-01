package com.realtimeportfolio.marketdata.producer;

import com.realtimeportfolio.common.config.kafka.KafkaTopics;
import com.realtimeportfolio.common.dto.StockPriceUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StackPriceUpdateProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void produce(StockPriceUpdateEvent stockPriceUpdateEvent) {
        if (stockPriceUpdateEvent.getCurrentPrice() == null) {
            log.error("Stock price is invalid for the ticker {} ", stockPriceUpdateEvent.getTickerSymbol());
            return;
        }
        log.info("Publishing stock price event  ");
        kafkaTemplate.send(KafkaTopics.STOCK_PRICE_UPDATES, stockPriceUpdateEvent);
        log.info("Stock price event published. tickerSymbol={}, price={}",
                stockPriceUpdateEvent.getTickerSymbol(),
                stockPriceUpdateEvent.getCurrentPrice()
        );
    }
}
