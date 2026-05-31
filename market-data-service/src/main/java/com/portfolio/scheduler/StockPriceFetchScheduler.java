package com.portfolio.scheduler;


import com.portfolio.producer.StockPriceEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockPriceFetchScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(StockPriceFetchScheduler.class);

    private final StockPriceEventProducer stockPriceEventProducer;

    public StockPriceFetchScheduler(StockPriceEventProducer stockPriceEventProducer) {
        this.stockPriceEventProducer = stockPriceEventProducer;
    }

    @Scheduled(fixedRateString = "${portfolio.stock-price-fetch.fixed-rate-ms:30000}")
    public void fetchStockPrices() {
        log.info("US9 stock price scheduler started");

        List<String> tickers = List.of(
                "TCS",
                "INFY",
                "RELIANCE",
                "HDFCBANK"
        );

        for (String ticker : tickers) {
            stockPriceEventProducer.fetchAndPublishPrice(ticker);
        }

        log.info("US9 stock price scheduler completed");
    }
}