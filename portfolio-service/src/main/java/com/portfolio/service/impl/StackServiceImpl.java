package com.portfolio.service.impl;

import com.portfolio.client.StackClient;
import com.portfolio.service.StackService;
import lombok.RequiredArgsConstructor;
import org.portfolio.dto.StockResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StackServiceImpl implements StackService {

    private StackClient stackClient;
    @Override
    public Optional<StockResponse> getStockByTickerSymbol(String stockCode) {
        return Optional.empty();
    }
}
