package com.realtimeportfolio.marketdata.scheduler;



import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.marketdata.producer.StockPriceEventProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class StockPriceFetchScheduler {
    private final StockPriceEventProducer stockPriceEventProducer;

//    @Value("${stack-market-ticker-list}")
//    private List<String> tickerList;

    public StockPriceFetchScheduler(StockPriceEventProducer stockPriceEventProducer) {
        this.stockPriceEventProducer = stockPriceEventProducer;
    }

//    @Scheduled(fixedRateString = "${portfolio.stock-price-fetch.fixed-rate-ms:30000}")
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