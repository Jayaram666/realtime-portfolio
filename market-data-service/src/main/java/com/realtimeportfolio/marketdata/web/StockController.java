package com.realtimeportfolio.marketdata.web;


import com.realtimeportfolio.common.dto.ApiResponse;
import com.realtimeportfolio.common.dto.StockPrice;
import com.realtimeportfolio.marketdata.producer.StockPriceEventProducer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.marketdata.client.RemoteStockClient;
import lombok.RequiredArgsConstructor;
import com.realtimeportfolio.common.dto.StockTickerDto;
import com.realtimeportfolio.common.dto.TickersDto;
import org.apache.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
@Slf4j
@RestController
public class StockController {

    private final RemoteStockClient stockClient;

    private final StockPriceEventProducer stockPriceEventProducer;

    @GetMapping("/tickers")
    public ApiResponse<List<StockTickerDto>> getStockTickers(HttpServletRequest httpServletRequest) {
        log.info("Market data ticker request received.");
        List<StockTickerDto> response = stockClient.getTopStocks();
        log.info("Market data ticker request completed. resultCount={}", response.size());
        return ApiResponse.<List<StockTickerDto>>builder()
                .message("Market data fetched successfully")
                .resource(httpServletRequest.getRequestURI())
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.SC_OK)
                .data(response).build();
    }

    @GetMapping("/price/{tickerSymbol}")
    public ApiResponse<StockPrice> getStockPrice(@PathVariable("tickerSymbol") String tickerSymbol,HttpServletRequest httpServletRequest) {
        log.info("Market data price request received. tickerSymbol={}", tickerSymbol);
        BigDecimal price = stockClient.getStockPrice(tickerSymbol).orElseThrow(InstantiationError::new);
        log.info("Market data price request completed. tickerSymbol={}, price={}", tickerSymbol, price);
        return ApiResponse.<StockPrice>builder().message("Price of stack fetched successfully").data(new StockPrice(price))
                .timestamp(LocalDateTime.now())
                .resource(httpServletRequest.getRequestURI())
                .status(HttpStatus.SC_OK)
                .build();

    }

    @GetMapping("/trigerEvent/{tickerSymbol}")
    public void triggerEvent(@PathVariable("tickerSymbol") String tickerSymbol) {
        log.info("Triggering events manually. tickerSymbol={}", tickerSymbol);
        stockPriceEventProducer.fetchAndPublishPrice(tickerSymbol);
        log.info("Triggering kafka events are completed. tickerSymbol={}", tickerSymbol);
    }
}
