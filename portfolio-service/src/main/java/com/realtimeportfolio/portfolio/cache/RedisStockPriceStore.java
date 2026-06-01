package com.realtimeportfolio.portfolio.cache;


import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.common.config.redis.RedisKeys;

import com.realtimeportfolio.common.dto.StockPriceUpdateEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Component
public class RedisStockPriceStore {

    private static final Duration PRICE_TTL = Duration.ofMinutes(2);

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisStockPriceStore(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveLatestPrice(StockPriceUpdateEvent event) {
        String key = buildKey(event.getTickerSymbol());

        redisTemplate.opsForValue().set(
                key,
                event,
                PRICE_TTL
        );
        log.debug("Latest stock price cached. tickerSymbol={}, ttlSeconds={}", event.getTickerSymbol(), PRICE_TTL.toSeconds());
    }

    public Optional<StockPriceUpdateEvent> getLatestPrice(String tickerSymbol) {
        String key = buildKey(tickerSymbol);

        Object value = redisTemplate.opsForValue().get(key);

        if (value instanceof StockPriceUpdateEvent event) {
            log.debug("Latest stock price cache hit. tickerSymbol={}", tickerSymbol);
            return Optional.of(event);
        }

        log.debug("Latest stock price cache miss. tickerSymbol={}", tickerSymbol);
        return Optional.empty();
    }

    private String buildKey(String tickerSymbol) {
        return RedisKeys.latestStockPriceKey(tickerSymbol.toUpperCase());
    }
}