package com.portfolio.kafka;


public final class KafkaTopics {

    private KafkaTopics() {
    }

    /**
     * Used in US9.
     * market-data-service publishes live stock price updates.
     * portfolio-service consumes these updates for realtime monitoring.
     */
    public static final String STOCK_PRICE_UPDATES = "stock-price-updates";
}