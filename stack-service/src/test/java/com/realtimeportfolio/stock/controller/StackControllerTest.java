package com.realtimeportfolio.stock.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.realtimeportfolio.common.exception.DuplicateResourceException;
import com.realtimeportfolio.common.exception.ResourceNotFoundException;
import com.realtimeportfolio.stock.dto.CreateStockRequest;
import com.realtimeportfolio.stock.dto.StockResponse;
import com.realtimeportfolio.stock.dto.StockValidationResponse;
import com.realtimeportfolio.stock.service.StockService;
import com.realtimeportfolio.stock.web.StockController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StockController.class)
public class StackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StockService stockService;

    @Test
    void shouldCreateStock() throws Exception {
        // Given
        CreateStockRequest request = new CreateStockRequest();
        request.setCompanyName("Apple Inc.");
        request.setTickerSymbol("AAPL");
        request.setExchange("NASDAQ");

        StockResponse response = new StockResponse(1L, "Apple Inc.", "AAPL", true);

        when(stockService.createStock(any(CreateStockRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.companyName").value(response.getCompanyName()))
                .andExpect(jsonPath("$.tickerSymbol").value(response.getTickerSymbol()))
                .andExpect(jsonPath("$.active").value(response.isActive()));
    }


    @Test
    void shouldNotCreateStockIfAlreadyExists() throws Exception {
        // Given
        CreateStockRequest request = new CreateStockRequest();
        request.setCompanyName("Apple Inc.");
        request.setTickerSymbol("AAPL");
        request.setExchange("NASDAQ");

        when(stockService.createStock(any(CreateStockRequest.class)))
                .thenThrow(new DuplicateResourceException("Stock already exists with ticker symbol: " + request.getTickerSymbol()));

        // When & Then
        mockMvc.perform(post("/api/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Stock already exists with ticker symbol: " + request.getTickerSymbol()));

    }


    @Test
    void shouldReturnAllStocks() throws Exception {

        List<StockResponse> stocks = List.of(
                new StockResponse(1L, "Apple Inc.", "AAPL", true),
                new StockResponse(2L, "Microsoft Corporation", "MSFT", true)
        );

        when(stockService.getAllActiveStocks()).thenReturn(stocks);

        mockMvc.perform(get("/api/stocks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(stocks.size()))
                .andExpect(jsonPath("$[0].id").value(stocks.get(0).getId()))
                .andExpect(jsonPath("$[0].companyName").value(stocks.get(0).getCompanyName()))
                .andExpect(jsonPath("$[0].tickerSymbol").value(stocks.get(0).getTickerSymbol()))
                .andExpect(jsonPath("$[0].active").value(stocks.get(0).isActive()))
                .andExpect(jsonPath("$[1].id").value(stocks.get(1).getId()))
                .andExpect(jsonPath("$[1].companyName").value(stocks.get(1).getCompanyName()))
                .andExpect(jsonPath("$[1].tickerSymbol").value(stocks.get(1).getTickerSymbol()))
                .andExpect(jsonPath("$[1].active").value(stocks.get(1).isActive()));
    }

    @Test
    void testGetStockByTickerSymbol() throws Exception {
        // Given
        String tickerSymbol = "AAPL";
        StockResponse response = new StockResponse(1L, "Apple Inc.", tickerSymbol, true);

        when(stockService.getStockByTickerSymbol(tickerSymbol)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/stocks/{tickerSymbol}", tickerSymbol)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.companyName").value(response.getCompanyName()))
                .andExpect(jsonPath("$.tickerSymbol").value(response.getTickerSymbol()))
                .andExpect(jsonPath("$.active").value(response.isActive()));
    }

    @Test
    void testGetStockByTickerSymbolNotFound() throws Exception {
        // Given
        String tickerSymbol = "AAPL";

        when(stockService.getStockByTickerSymbol(tickerSymbol)).thenThrow(new ResourceNotFoundException("Stock not found with ticker symbol: " + tickerSymbol));

        // When & Then
        mockMvc.perform(get("/api/stocks/{tickerSymbol}", tickerSymbol)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Stock not found with ticker symbol: " + tickerSymbol));
    }

    @Test
    void testValidateTickerSymbol() throws Exception {
        // Given
        String tickerSymbol = "AAPL";
        when(stockService.validateTickerSymbol(tickerSymbol)).thenReturn(new StockValidationResponse(tickerSymbol, true));

        // When & Then
        mockMvc.perform(get("/api/stocks/{tickerSymbol}/valid", tickerSymbol)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tickerSymbol").value(tickerSymbol))
                .andExpect(jsonPath("$.valid").value(true));
    }
}
