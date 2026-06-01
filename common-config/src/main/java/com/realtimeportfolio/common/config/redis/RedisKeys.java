package com.realtimeportfolio.common.config.redis;


public final class RedisKeys {

    private RedisKeys() {
    }

    public static String latestStockPriceKey(String tickerSymbol) {
        return "stock:latest-price:" + tickerSymbol.toUpperCase();
    }

    public static String portfolioMonitoringKey(Long userId) {
        return "portfolio:monitoring:user:" + userId;
    }
}