package com.realtimeportfolio.portfolio.web;


import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.portfolio.service.UserPortfolioService;
import jakarta.validation.Valid;

import com.realtimeportfolio.common.dto.CreatePortfolioRequest;
import com.realtimeportfolio.common.dto.PortfolioResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final UserPortfolioService userPortfolioService;

    public PortfolioController(UserPortfolioService userPortfolioService) {
        this.userPortfolioService = userPortfolioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PortfolioResponse createPortfolio(
            @Valid @RequestBody CreatePortfolioRequest request,@RequestHeader("X-User-Id") Long authenticatedUserId
    ) {

        log.info("Create portfolio API called. userId={}, tickerSymbol={}", authenticatedUserId, request.getTickerSymbol());

        PortfolioResponse response = userPortfolioService.createPortfolio(authenticatedUserId, request);
        log.info("Create portfolio API completed. userId={}, tickerSymbol={}", authenticatedUserId, response.getTickerSymbol());
        return response;
    }

    @GetMapping
    public List<PortfolioResponse> getMyPortfolio(@RequestHeader("X-User-Id") Long authenticatedUserId) {
        log.info("Get portfolio API called. userId={}", authenticatedUserId);

        List<PortfolioResponse> response = userPortfolioService.getMyPortfolio(authenticatedUserId);
        log.info("Get portfolio API completed. userId={}, count={}", authenticatedUserId, response.size());
        return response;
    }
}