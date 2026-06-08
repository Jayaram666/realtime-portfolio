package com.realtimeportfolio.portfolio.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realtimeportfolio.common.config.redis.RedisKeys;
import com.realtimeportfolio.common.dto.StockPriceUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisStockPriceStore {

    private static final Duration PRICE_TTL = Duration.ofMinutes(2);

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void saveLatestPrice(StockPriceUpdateEvent event) {
        String tickerSymbol = event.getTickerSymbol();
        String key = buildKey(tickerSymbol);

        try {
            String json = objectMapper.writeValueAsString(event);

            redisTemplate.opsForValue().set(key, json, PRICE_TTL);

            log.info(
                    "Latest stock price cached successfully. tickerSymbol={}, key={}, price={}, ttlSeconds={}",
                    tickerSymbol,
                    key,
                    event.getCurrentPrice(),
                    PRICE_TTL.toSeconds()
            );

        } catch (JsonProcessingException ex) {
            log.error(
                    "Failed to serialize stock price event before caching. tickerSymbol={}, key={}",
                    tickerSymbol,
                    key,
                    ex
            );

            throw new IllegalStateException("Failed to cache latest stock price for tickerSymbol=" + tickerSymbol, ex);
        }
    }

    public Optional<StockPriceUpdateEvent> getLatestPrice(String tickerSymbol) {
        String key = buildKey(tickerSymbol);

        log.debug("Reading latest stock price from Redis cache. tickerSymbol={}, key={}", tickerSymbol, key);

        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            log.info("Latest stock price cache miss. tickerSymbol={}, key={}", tickerSymbol, key);
            return Optional.empty();
        }

        if (!(value instanceof String json)) {
            log.warn(
                    "Unexpected Redis value type for latest stock price. tickerSymbol={}, key={}, expectedType={}, actualType={}. Deleting invalid cache entry.",
                    tickerSymbol,
                    key,
                    String.class.getName(),
                    value.getClass().getName()
            );

            redisTemplate.delete(key);
            return Optional.empty();
        }

        try {
            StockPriceUpdateEvent event = objectMapper.readValue(json, StockPriceUpdateEvent.class);

            log.info(
                    "Latest stock price cache hit. tickerSymbol={}, key={}, price={}",
                    tickerSymbol,
                    key,
                    event.getCurrentPrice()
            );

            return Optional.of(event);

        } catch (JsonProcessingException ex) {
            log.error(
                    "Failed to deserialize cached stock price. tickerSymbol={}, key={}. Deleting invalid cache entry.",
                    tickerSymbol,
                    key,
                    ex
            );

            redisTemplate.delete(key);
            return Optional.empty();
        }
    }

    public boolean exists(String tickerSymbol) {
        String key = buildKey(tickerSymbol);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void deleteLatestPrice(String tickerSymbol) {
        String key = buildKey(tickerSymbol);

        Boolean deleted = redisTemplate.delete(key);

        log.info(
                "Deleted latest stock price cache entry. tickerSymbol={}, key={}, deleted={}",
                tickerSymbol,
                key,
                deleted
        );
    }

    public void logCacheStatus(String tickerSymbol) {
        String key = buildKey(tickerSymbol);

        Boolean exists = redisTemplate.hasKey(key);
        Long ttlSeconds = redisTemplate.getExpire(key);
        DataType type = redisTemplate.type(key);

        log.info(
                "Redis stock price cache status. tickerSymbol={}, key={}, exists={}, ttlSeconds={}, type={}",
                tickerSymbol,
                key,
                exists,
                ttlSeconds,
                type
        );
    }

    private String buildKey(String tickerSymbol) {
        return RedisKeys.latestStockPriceKey(tickerSymbol.toUpperCase());
    }
}