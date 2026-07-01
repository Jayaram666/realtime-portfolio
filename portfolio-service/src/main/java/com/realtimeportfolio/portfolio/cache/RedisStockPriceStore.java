package com.realtimeportfolio.portfolio.cache;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realtimeportfolio.common.config.redis.RedisKeys;
import com.realtimeportfolio.common.dto.StockPriceUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisStockPriceStore {

    private static final Duration PRICE_TTL = Duration.ofMinutes(2);

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void process(List<StockPriceUpdateEvent> eventList) {
        if (eventList == null || eventList.isEmpty()) {
            return;
        }

        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            @SuppressWarnings("unchecked")
            RedisSerializer<String> keySerializer = (RedisSerializer<String>) redisTemplate.getKeySerializer();
            @SuppressWarnings("unchecked")
            RedisSerializer<Object> valueSerializer = (RedisSerializer<Object>) redisTemplate.getValueSerializer();

            for (StockPriceUpdateEvent event : eventList) {
                if (event == null || event.getTickerSymbol() == null) {
                    continue;
                }

                String key = buildKey(event.getTickerSymbol());
                try {
                    String json = objectMapper.writeValueAsString(event);

                    byte[] rawKey = keySerializer.serialize(key);
                    byte[] rawValue = valueSerializer.serialize(json);

                    if (rawKey != null && rawValue != null) {
                        connection.stringCommands().setEx(rawKey, PRICE_TTL.toSeconds(), rawValue);
                    }
                } catch (JsonProcessingException ex) {
                    log.error("Failed to serialize ticker symbol {} during bulk pipeline processing.", event.getTickerSymbol(), ex);
                }
            }
            return null;
        });
    }

    /**
     * Fetches the latest cached price payload for a ticker symbol.
     */
    public Optional<StockPriceUpdateEvent> getLatestPrice(String tickerSymbol) {
        if (tickerSymbol == null) {
            return Optional.empty();
        }

        String key = buildKey(tickerSymbol);
        log.debug("Reading latest stock price from Redis cache. tickerSymbol={}, key={}", tickerSymbol, key);

        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            log.debug("Latest stock price cache miss. tickerSymbol={}, key={}", tickerSymbol, key);
            return Optional.empty();
        }

        if (!(value instanceof String json)) {
            log.warn("Unexpected Redis value type for latest stock price. tickerSymbol={}, key={}, actualType={}. Deleting invalid cache entry.",
                    tickerSymbol, key, value.getClass().getName());

            redisTemplate.delete(key);
            return Optional.empty();
        }

        try {
            StockPriceUpdateEvent event = objectMapper.readValue(json, StockPriceUpdateEvent.class);
            log.debug("Latest stock price cache hit. tickerSymbol={}, key={}, price={}", tickerSymbol, key, event.getCurrentPrice());
            return Optional.of(event);

        } catch (JsonProcessingException ex) {
            log.error("Failed to deserialize cached stock price. tickerSymbol={}, key={}. Deleting invalid cache entry.", tickerSymbol, key, ex);
            redisTemplate.delete(key);
            return Optional.empty();
        }
    }

    private String buildKey(String tickerSymbol) {
        return RedisKeys.latestStockPriceKey(tickerSymbol.toUpperCase());
    }
}
