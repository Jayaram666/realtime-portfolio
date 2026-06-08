package com.realtimeportfolio.stock.service;

import com.realtimeportfolio.common.exception.BusinessException;
import com.realtimeportfolio.common.exception.DuplicateResourceException;
import com.realtimeportfolio.common.exception.ResourceNotFoundException;
import com.realtimeportfolio.stock.dto.CreateStockRequest;
import com.realtimeportfolio.stock.dto.StockResponse;
import com.realtimeportfolio.stock.dto.StockStatusUpdateRequest;
import com.realtimeportfolio.stock.entity.Stock;
import com.realtimeportfolio.stock.repo.StockRepository;
import com.realtimeportfolio.stock.service.impl.StockServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class StackServiceImplTest {

    @Mock
    private StockRepository stockRepository;
    @InjectMocks
    private StockServiceImpl stockService;

    @Test
    public void shouldCreateStack() {

        Stock stock = new Stock();
        stock.setId(1L);
        stock.setTickerSymbol("TCS");
        stock.setCreatedAt(LocalDateTime.now());
        stock.setCompanyName("Tata Consultancy Services");
        stock.setActive(true);
        stock.setExchange("NSE");

        CreateStockRequest createStockRequest = new CreateStockRequest();
        createStockRequest.setTickerSymbol("TCS");
        createStockRequest.setCompanyName("Tata Consultancy Services");
        createStockRequest.setExchange("NSE");

        Mockito.when(stockRepository.save(Mockito.any(Stock.class))).thenReturn(stock);

        StockResponse createdStock = stockService.createStock(createStockRequest);

        assert createdStock.getId().equals(stock.getId());
        assert createdStock.getTickerSymbol().equals(stock.getTickerSymbol());
        assert createdStock.getCompanyName().equals(stock.getCompanyName());
        Mockito.verify(stockRepository, Mockito.times(1)).save(Mockito.any(Stock.class));

    }

    @Test
    public void shouldNotCreateIfStockAlreadyExists() {
        CreateStockRequest createStockRequest = new CreateStockRequest();
        createStockRequest.setTickerSymbol("TCS");
        createStockRequest.setCompanyName("Tata Consultancy Services");
        createStockRequest.setExchange("NSE");
        Mockito.when(stockRepository.existsByTickerSymbolIgnoreCaseAndExchangeIgnoreCase(Mockito.any())).thenReturn(true);
        Assertions.assertThrows(DuplicateResourceException.class, () -> {
            stockService.createStock(createStockRequest);
        }, "Expected createStock to throw, but it didn't");
        Mockito.verify(stockRepository, Mockito.times(0)).save(Mockito.any(Stock.class));
    }

    @Test
    public void shouldNotCreateIfTickerSymbolIsEmpty() {
        CreateStockRequest createStockRequest = new CreateStockRequest();
        createStockRequest.setCompanyName("Tata Consultancy Services");
        createStockRequest.setExchange("NSE");
        Assertions.assertThrows(BusinessException.class, () -> {
            stockService.createStock(createStockRequest);
        }, "Expected createStock to throw, but it didn't");
        Mockito.verify(stockRepository, Mockito.times(0)).save(Mockito.any(Stock.class));
    }

    @Test
    public void shouldUpdateStock() {
        Stock stock = new Stock();
        stock.setId(1L);
        stock.setTickerSymbol("TCS");
        stock.setCreatedAt(LocalDateTime.now());
        stock.setCompanyName("Tata Consultancy Services");
        stock.setActive(true);
        stock.setExchange("NSE");

        StockStatusUpdateRequest stockStatusUpdateRequest = new StockStatusUpdateRequest();
        stockStatusUpdateRequest.setActive(false);

        Mockito.when(stockRepository.findById(Mockito.any(Long.class))).thenReturn(java.util.Optional.of(stock));
        Mockito.when(stockRepository.save(Mockito.any(Stock.class))).thenReturn(stock);
        StockResponse updatedStock = stockService.updateStockStatus(1L, stockStatusUpdateRequest);
        assert updatedStock.getId().equals(stock.getId());
        assert !updatedStock.isActive();
    }

    @Test
    public void shouldNotUpdateIfStockNotFound() {
        StockStatusUpdateRequest stockStatusUpdateRequest = new StockStatusUpdateRequest();
        stockStatusUpdateRequest.setActive(false);
        Mockito.when(stockRepository.findById(Mockito.any(Long.class))).thenReturn(java.util.Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            stockService.updateStockStatus(1L, stockStatusUpdateRequest);
        }, "Expected updateStockStatus to throw, but it didn't");
        Mockito.verify(stockRepository, Mockito.times(0)).save(Mockito.any(Stock.class));
    }

    @Test
    public void shouldReturnStockByTickerSymbol() {
        Stock stock = new Stock();
        stock.setId(1L);
        stock.setTickerSymbol("TCS");
        stock.setCreatedAt(LocalDateTime.now());
        stock.setCompanyName("Tata Consultancy Services");
        stock.setActive(true);
        stock.setExchange("NSE");

        Mockito.when(stockRepository.findByTickerSymbolIgnoreCaseAndActiveTrue(Mockito.any())).thenReturn(java.util.Optional.of(stock));
        StockResponse stockResponse = stockService.getStockByTickerSymbol("TCS");
        assert stockResponse.getId().equals(stock.getId());
        assert stockResponse.getTickerSymbol().equals(stock.getTickerSymbol());
        assert stockResponse.getCompanyName().equals(stock.getCompanyName());
        Mockito.verify(stockRepository, Mockito.times(1)).findByTickerSymbolIgnoreCaseAndActiveTrue(Mockito.any());
    }

    @Test
    public void testGetAllSacks(){

        Stock stock1 = new Stock();
            stock1.setId(1L);
            stock1.setTickerSymbol("TCS");
            stock1.setCreatedAt(LocalDateTime.now());
            stock1.setCompanyName("Tata Consultancy Services");
            stock1.setActive(true);
            stock1.setExchange("NSE");

            Stock stock2 = new Stock();
            stock2.setId(2L);
            stock2.setTickerSymbol("INFY");
            stock2.setCreatedAt(LocalDateTime.now());
            stock2.setCompanyName("Infosys");
            stock2.setActive(true);
            stock2.setExchange("NSE");

            Mockito.when(stockRepository.findByActiveTrueOrderByCompanyNameAsc()).thenReturn(java.util.List.of(stock1,stock2));
            java.util.List<StockResponse> allStocks = stockService.getAllActiveStocks();
            assert allStocks.size()==2;

    }
}
