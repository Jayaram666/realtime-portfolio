package com.realtimeportfolio.marketdata.stream;


import com.realtimeportfolio.common.util.StockSymbolTokenMap;
import com.upstox.feeder.MarketDataStreamerV3;
import com.upstox.feeder.constants.Mode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpstoxWebSocketClient {

    private final MarketDataStreamerV3 streamer;
    private final UpstoxMarketDataHandler marketDataHandler;

    public void startStreaming() {
        log.info("Kick starting web socket for real time stack data");

        // Wire up the separate listener handler
        streamer.setOnMarketUpdateListener(marketDataHandler::handleMarketUpdate);

        streamer.setOnErrorListener(ex -> log.error("WebSocket Error: {}", ex.getMessage(), ex));
        streamer.setOnReconnectingListener(reason -> log.info("Reconnecting due to: {}", reason));

        streamer.setOnOpenListener(() -> {
            log.info("Connected to Upstox WebSocket successfully!");
            subscribeToInstruments();
        });

        streamer.connect();
    }

    private void subscribeToInstruments() {
        try {
            Set<String> instrumentKeys = new HashSet<>(StockSymbolTokenMap.getAllSymbols().values());
            streamer.subscribe(instrumentKeys, Mode.FULL);
            log.info("Successfully subscribed to {} instruments.", instrumentKeys.size());
        } catch (Exception e) {
            log.error("Subscription failed: {}", e.getMessage());
        }
    }

    public void stopStreaming() {
        log.info("Stopping websocket of stack price data");
        if (streamer != null) {
            streamer.disconnect();
        }
        log.info("Stopped web socket streaming");
    }
}
