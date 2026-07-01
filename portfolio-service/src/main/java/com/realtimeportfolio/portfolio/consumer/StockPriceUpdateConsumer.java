package com.realtimeportfolio.portfolio.consumer;


import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.portfolio.cache.RedisStockPriceStore;
import com.realtimeportfolio.common.config.kafka.KafkaConsumerGroups;
import com.realtimeportfolio.common.config.kafka.KafkaTopics;

import com.realtimeportfolio.common.dto.StockPriceUpdateEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class StockPriceUpdateConsumer {
    private final RedisStockPriceStore redisStockPriceStore;

    public StockPriceUpdateConsumer(RedisStockPriceStore redisStockPriceStore) {
        this.redisStockPriceStore = redisStockPriceStore;
    }

    @KafkaListener(
            topics = KafkaTopics.STOCK_PRICE_UPDATES,
            groupId = KafkaConsumerGroups.PORTFOLIO_SERVICE_GROUP
    )
    public void consume(List<StockPriceUpdateEvent> events) {
        log.info("Stock price event consumed. the size of events {} ", events);
        redisStockPriceStore.process(events);
        log.info("Latest stock price stored in Redis");
    }
}