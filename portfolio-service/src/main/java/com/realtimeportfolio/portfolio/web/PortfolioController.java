package com.realtimeportfolio.portfolio.web;


import com.realtimeportfolio.common.dto.ApiResponse;
import com.realtimeportfolio.portfolio.dto.BulkPortfolioRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.portfolio.service.UserPortfolioService;
import jakarta.validation.Valid;

import com.realtimeportfolio.common.dto.CreatePortfolioRequest;
import com.realtimeportfolio.common.dto.PortfolioResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
    public ApiResponse<List<PortfolioResponse>> createPortfolio(
            @Valid @RequestBody BulkPortfolioRequest request, HttpServletRequest httpServletRequest, @RequestHeader("X-User-Id") UUID authenticatedUserId
    ) {

        log.info("Create portfolio API called. userId={}, tickerSymbol={}", authenticatedUserId, request.toString());
        List<PortfolioResponse> response = userPortfolioService.createOrUpdatePortfolioBatch(authenticatedUserId, request.getItems());
        log.info("Create portfolio API completed");
        return ApiResponse.<List<PortfolioResponse>>builder()
                .status(HttpStatus.CREATED.value())
                .resource(httpServletRequest.getRequestURI())
                .timestamp(LocalDateTime.now())
                .message("Successfully portfolio updated/created")
                .data(response)
                .build();
    }

    @GetMapping
    public ApiResponse<List<PortfolioResponse>> getMyPortfolio(HttpServletRequest httpServletRequest, @RequestHeader("X-User-Id") UUID authenticatedUserId) {
        log.info("Get portfolio API called. userId={}", authenticatedUserId);

        List<PortfolioResponse> response = userPortfolioService.getMyPortfolio(authenticatedUserId);
        log.info("Get portfolio API completed. userId={}, count={}", authenticatedUserId, response.size());
        return ApiResponse.<List<PortfolioResponse>>builder()
                .status(HttpStatus.OK.value())
                .resource(httpServletRequest.getRequestURI())
                .timestamp(LocalDateTime.now())
                .message("Successfully portfolio data fetched")
                .data(response)
                .build();
    }

    @DeleteMapping("/{tickerSymbol}")
    public ApiResponse<String> deleteStock(HttpServletRequest httpServletRequest,
                                           @RequestHeader("X-User-Id") UUID authenticatedUserId,
                                           @PathVariable("tickerSymbol") String stockName
    ) {
        log.info("Delete portfolio API called. userId={}", authenticatedUserId);
        String message = "Successfully portfolio data deleted with the stock:" + stockName;
        if (!userPortfolioService.deletedMyPortFolio(authenticatedUserId, stockName)) {
            message = "Couldn't delete the portfolio with the stock:" + stockName;
        }
        log.info("Deleted portfolio api is completed");

        return ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .resource(httpServletRequest.getRequestURI())
                .timestamp(LocalDateTime.now())
                .message(message)
                .build();
    }
}