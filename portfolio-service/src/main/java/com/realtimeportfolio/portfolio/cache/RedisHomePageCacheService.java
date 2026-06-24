package com.realtimeportfolio.portfolio.cache;


import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.common.dto.MenuResponse;
import com.realtimeportfolio.common.dto.StockTickerDto;
import com.realtimeportfolio.common.dto.UserDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class RedisHomePageCacheService implements HomePageCacheService {

    private static final String USER_KEY_PREFIX = "homepage:user:";
    private static final String MENU_KEY_PREFIX = "homepage:menus:";
    private static final String STOCK_TICKERS_KEY = "homepage:stock-tickers";

    private static final Duration USER_CACHE_TTL = Duration.ofMinutes(30);
    private static final Duration MENU_CACHE_TTL = Duration.ofMinutes(30);
    private static final Duration STOCK_TICKER_CACHE_TTL = Duration.ofMinutes(5);

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisHomePageCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void cacheUser(String email, UserDto user) {
        String key = USER_KEY_PREFIX + normalizeEmail(email);

        redisTemplate.opsForValue().set(
                key,
                user,
                USER_CACHE_TTL
        );
        log.debug("Cached homepage user data. email={}, ttlSeconds={}", email, USER_CACHE_TTL.toSeconds());
    }

    @Override
    public Optional<UserDto> getCachedUser(String email) {
        String key = USER_KEY_PREFIX + normalizeEmail(email);

        Object value = redisTemplate.opsForValue().get(key);

        if (value instanceof UserDto userDto) {
            log.debug("Homepage user cache hit. email={}", email);
            return Optional.of(userDto);
        }

        log.debug("Homepage user cache miss. email={}", email);
        return Optional.empty();
    }

    @Override
    public void cacheUserMenus(UUID userId, List<MenuResponse> menus) {
        String key = MENU_KEY_PREFIX + userId;

        redisTemplate.opsForValue().set(
                key,
                menus,
                MENU_CACHE_TTL
        );
        log.debug("Cached homepage menus. userId={}, menuCount={}, ttlSeconds={}",
                userId,
                menus != null ? menus.size() : 0,
                MENU_CACHE_TTL.toSeconds());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<List<MenuResponse>> getCachedUserMenus(UUID userId) {
        String key = MENU_KEY_PREFIX + userId;

        Object value = redisTemplate.opsForValue().get(key);

        if (value instanceof List<?>) {
            log.debug("Homepage menu cache hit. userId={}", userId);
            return Optional.of((List<MenuResponse>) value);
        }

        log.debug("Homepage menu cache miss. userId={}", userId);
        return Optional.empty();
    }

    @Override
    public void cacheStockTickers(List<StockTickerDto> stockTickers) {
        redisTemplate.opsForValue().set(
                STOCK_TICKERS_KEY,
                stockTickers,
                STOCK_TICKER_CACHE_TTL
        );
        log.debug("Cached homepage stock tickers. count={}, ttlSeconds={}",
                stockTickers != null ? stockTickers.size() : 0,
                STOCK_TICKER_CACHE_TTL.toSeconds());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<List<StockTickerDto>> getCachedStockTickers() {
        Object value = redisTemplate.opsForValue().get(STOCK_TICKERS_KEY);

        if (value instanceof List<?>) {
            log.debug("Homepage stock ticker cache hit");
            return Optional.of((List<StockTickerDto>) value);
        }

        log.debug("Homepage stock ticker cache miss");
        return Optional.empty();
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}