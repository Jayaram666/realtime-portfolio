package com.portfolio.web;

import com.portfolio.service.UserPortfolioService;
import jakarta.validation.Valid;

import org.portfolio.dto.CreatePortfolioRequest;
import org.portfolio.dto.PortfolioResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @Valid @RequestBody CreatePortfolioRequest request
    ) {
        /*
         * For now hardcoded.
         * Later we will take this from Spring Security authenticated user.
         */
        Long authenticatedUserId = 1L;

        return userPortfolioService.createPortfolio(authenticatedUserId, request);
    }

    @GetMapping
    public List<PortfolioResponse> getMyPortfolio() {
        Long authenticatedUserId = 1L;

        return userPortfolioService.getMyPortfolio(authenticatedUserId);
    }
}