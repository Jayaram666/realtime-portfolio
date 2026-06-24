package com.realtimeportfolio.marketdata.client;

import com.upstox.ApiClient;
import com.upstox.Configuration;
import com.upstox.auth.OAuth;
import com.upstox.feeder.MarketDataStreamerV3;
import com.upstox.feeder.constants.Mode;

import java.util.HashSet;
import java.util.Set;

public class UpstoxWebSocketClient {

    public static void main(String[] args) {
        // 1. Fetch your valid access token via the standard OAuth flow
        String accessToken = "test";
        // 2. Configure the default API client

        ApiClient apiClient = new ApiClient();
        apiClient.setAccessToken(accessToken);


        // 3. Define the instrument keys you want to stream
        Set<String> instrumentKeys = new HashSet<>();
        instrumentKeys.add("NSE_INDEX|Nifty 50");
        instrumentKeys.add("NSE_INDEX|Nifty Bank");

        try {
            // 4. Initialize the Market Data Streamer
            MarketDataStreamerV3 streamer = new MarketDataStreamerV3(apiClient);

            // Optional: Enable auto-reconnection capability
            streamer.autoReconnect(true);

            // 5. Define listener callbacks for handling data stream cycles
            streamer.setOnMarketUpdateListener(marketUpdate -> {
                // The incoming market stream payload is decoded via Protobuf
                System.out.println("Received Live Tick: " + marketUpdate.toString());
            });

            streamer.setOnErrorListener(exception -> {
                System.err.println("WebSocket Error occurred: " + exception.getMessage());
            });

            streamer.setOnOpenListener(() -> {
                System.out.println("Connected to Upstox WebSocket successfully!");

                // 6. Subscribe to instruments immediately upon establishing connection
                try {
                    streamer.subscribe(instrumentKeys, Mode.FULL);
                } catch (Exception e) {
                    System.err.println("Subscription failed: " + e.getMessage());
                }
            });

            streamer.setOnReconnectingListener(reason -> {
                System.out.println("Disconnected from server. Reason: " + reason);
            });

            // 7. Initiate the asynchronous connection
            streamer.connect();

        } catch (Exception e) {
            System.err.println("Initialization failed: " + e.getMessage());
        }
    }
}
