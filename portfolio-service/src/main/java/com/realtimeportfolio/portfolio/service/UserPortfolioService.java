package com.realtimeportfolio.portfolio.service;

import com.realtimeportfolio.portfolio.entity.UserPortfolio;
import com.realtimeportfolio.portfolio.repo.UserPortfolioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.common.dto.CreatePortfolioRequest;
import com.realtimeportfolio.common.dto.PortfolioResponse;
import com.realtimeportfolio.common.dto.StockResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPortfolioService {

    private final StackService stackService;
    private final UserPortfolioRepository userPortfolioRepository;

    @Transactional
    public PortfolioResponse createPortfolio(
            Long userId,
            CreatePortfolioRequest request
    ) {
        String tickerSymbol = request.getTickerSymbol().trim().toUpperCase();
        StockResponse stock = stackService.getStockByTickerSymbol(tickerSymbol)
                .orElseThrow(() -> new RuntimeException(
                        "Invalid stock selected: " + tickerSymbol
                ));

        boolean alreadyExists = userPortfolioRepository
                .findByUserIdAndTickerSymbolIgnoreCase(userId, tickerSymbol)
                .isPresent();

        if (alreadyExists) {
            throw new RuntimeException(
                    "Stock already exists in your portfolio: " + tickerSymbol
            );
        }

        UserPortfolio portfolio = new UserPortfolio();
        portfolio.setUserId(userId);
        portfolio.setCompanyName(stock.getCompanyName());
        portfolio.setTickerSymbol(stock.getTickerSymbol());
        portfolio.setQuantity(request.getQuantity());
        portfolio.setBuyingPrice(request.getBuyingPrice());

        UserPortfolio savedPortfolio = userPortfolioRepository.save(portfolio);

        log.info("Portfolio created successfully. userId={}, tickerSymbol={}, portfolioId={}",
                userId,
                savedPortfolio.getTickerSymbol(),
                savedPortfolio.getId()
        );

        return toResponse(savedPortfolio);
    }

    @Transactional(readOnly = true)
    public List<PortfolioResponse> getMyPortfolio(Long userId) {
        log.info("Fetching portfolio for userId={}", userId);

        return userPortfolioRepository.findByUserIdOrderByCompanyNameAsc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private PortfolioResponse toResponse(UserPortfolio portfolio) {
        return new PortfolioResponse(
                portfolio.getId(),
                portfolio.getCompanyName(),
                portfolio.getTickerSymbol(),
                portfolio.getQuantity(),
                portfolio.getBuyingPrice(),
                portfolio.getTotalInvestedAmount()
        );
    }
}