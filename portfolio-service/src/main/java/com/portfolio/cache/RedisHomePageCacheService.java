package com.portfolio.cache;

import org.portfolio.dto.MenuResponse;
import org.portfolio.dto.StockTickerDto;
import org.portfolio.dto.UserDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

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
    }

    @Override
    public Optional<UserDto> getCachedUser(String email) {
        String key = USER_KEY_PREFIX + normalizeEmail(email);

        Object value = redisTemplate.opsForValue().get(key);

        if (value instanceof UserDto userDto) {
            return Optional.of(userDto);
        }

        return Optional.empty();
    }

    @Override
    public void cacheUserMenus(Long userId, List<MenuResponse> menus) {
        String key = MENU_KEY_PREFIX + userId;

        redisTemplate.opsForValue().set(
                key,
                menus,
                MENU_CACHE_TTL
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<List<MenuResponse>> getCachedUserMenus(Long userId) {
        String key = MENU_KEY_PREFIX + userId;

        Object value = redisTemplate.opsForValue().get(key);

        if (value instanceof List<?>) {
            return Optional.of((List<MenuResponse>) value);
        }

        return Optional.empty();
    }

    @Override
    public void cacheStockTickers(List<StockTickerDto> stockTickers) {
        redisTemplate.opsForValue().set(
                STOCK_TICKERS_KEY,
                stockTickers,
                STOCK_TICKER_CACHE_TTL
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<List<StockTickerDto>> getCachedStockTickers() {
        Object value = redisTemplate.opsForValue().get(STOCK_TICKERS_KEY);

        if (value instanceof List<?>) {
            return Optional.of((List<StockTickerDto>) value);
        }

        return Optional.empty();
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}