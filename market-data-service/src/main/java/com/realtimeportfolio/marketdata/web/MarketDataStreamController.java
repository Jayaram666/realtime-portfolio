package com.realtimeportfolio.marketdata.web;


import com.realtimeportfolio.marketdata.stream.UpstoxWebSocketClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
public class MarketDataStreamController {

    private final UpstoxWebSocketClient upstoxWebSocketClient;

    @PostMapping("/start")
    public ResponseEntity<String> startStreaming() {
        log.info("REST request to start Upstox streaming");
        try {
            upstoxWebSocketClient.startStreaming();
            return ResponseEntity.ok("Upstox WebSocket streaming initialization started successfully.");
        } catch (Exception e) {
            log.error("Failed to start streaming via REST endpoint: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Error starting stream: " + e.getMessage());
        }
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stopStreaming() {
        log.info("REST request to stop Upstox streaming");
        try {
            upstoxWebSocketClient.stopStreaming();
            return ResponseEntity.ok("Upstox WebSocket streaming stopped successfully.");
        } catch (Exception e) {
            log.error("Failed to stop streaming via REST endpoint: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Error stopping stream: " + e.getMessage());
        }
    }
}

