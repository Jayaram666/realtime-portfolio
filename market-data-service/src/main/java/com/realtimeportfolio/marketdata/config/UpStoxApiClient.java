package com.realtimeportfolio.marketdata.config;

import com.upstox.ApiClient;
import com.upstox.feeder.MarketDataStreamerV3;
import io.swagger.client.api.MarketQuoteApi;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UpStoxApiClient {

    private final UpstoxProperties upstoxProperties;

    @Bean
    public ApiClient apiClient() {
        String accessToken = upstoxProperties.getAccessToken();
        ApiClient apiClient = new ApiClient();
        apiClient.setAccessToken(accessToken);
        return apiClient;
    }

    @Bean
    public MarketDataStreamerV3 marketDataStreamer(ApiClient apiClient) {
        MarketDataStreamerV3 streamer = new MarketDataStreamerV3(apiClient);
        streamer.autoReconnect(true);
        return streamer;
    }

    @Bean
    public MarketQuoteApi createMarketQuoteApi(ApiClient apiClient) {
        return new MarketQuoteApi(apiClient);
    }
}


