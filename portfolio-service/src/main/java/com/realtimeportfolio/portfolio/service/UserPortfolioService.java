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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPortfolioService {

    private final StackService stackService;
    private final UserPortfolioRepository userPortfolioRepository;

    @Transactional
    public List<PortfolioResponse> createOrUpdatePortfolioBatch(
            UUID userId,
            List<CreatePortfolioRequest> requests
    ) {
        if (requests == null || requests.isEmpty()) {
            log.warn("Empty portfolio request batch received for userId={}", userId);
            return List.of();
        }

        log.info("Processing portfolio batch upsert for userId={}, batchSize={}", userId, requests.size());

        // 1. Fetch entire user portfolio in one database trip to avoid N+1 queries in the loop
        List<UserPortfolio> existingPortfolios = userPortfolioRepository.findByUserIdOrderByCompanyNameAsc(userId);

        // Map it by Ticker Symbol for O(1) in-memory lookups
        Map<String, UserPortfolio> existingPortfolioMap = existingPortfolios.stream()
                .collect(Collectors.toMap(
                        p -> p.getTickerSymbol().toUpperCase(),
                        p -> p,
                        (existing, replacement) -> existing
                ));
        System.out.println("Existing portfolio :"+existingPortfolios);
        List<UserPortfolio> entitiesToSave = new ArrayList<>();

        for (CreatePortfolioRequest request : requests) {
            if (request == null || request.getTickerSymbol() == null) continue;

            String tickerSymbol = request.getTickerSymbol().trim().toUpperCase();

            // 2. Fetch or validate stock from core system metadata
            StockResponse stock = stackService.getStockByTickerSymbol(tickerSymbol)
                    .orElseThrow(() -> new RuntimeException("Invalid stock selected: " + tickerSymbol));

            if (existingPortfolioMap.containsKey(tickerSymbol)) {
                // 3. UPDATE CASE: Calculate new volume metrics and average cost basis safely
                UserPortfolio existingRecord = existingPortfolioMap.get(tickerSymbol);

                int currentQty = existingRecord.getQuantity();
                int additionalQty = request.getQuantity();
                int combinedQty = currentQty + additionalQty;

                BigDecimal currentCostBasis = existingRecord.getBuyingPrice().multiply(BigDecimal.valueOf(currentQty));
                BigDecimal incomingCostBasis = request.getBuyingPrice().multiply(BigDecimal.valueOf(additionalQty));
                BigDecimal combinedCostBasis = currentCostBasis.add(incomingCostBasis);

                // Safe division to find weighted average buying price
                BigDecimal newAveragePrice = combinedCostBasis.divide(BigDecimal.valueOf(combinedQty), 4, RoundingMode.HALF_UP);

                existingRecord.setQuantity(combinedQty);
                existingRecord.setBuyingPrice(newAveragePrice);

                entitiesToSave.add(existingRecord);
                log.info("Updated existing portfolio item. userId={}, tickerSymbol={}, newQty={}, newAvgPrice={}",
                        userId, tickerSymbol, combinedQty, newAveragePrice);
            } else {
                // 4. INSERT CASE: Clean map parsing for brand new assets
                UserPortfolio brandNewPortfolio = mapToUserPortfolioEntity(userId, request, stock);
                entitiesToSave.add(brandNewPortfolio);

                // Track in-flight additions to handle potential duplicate entries within the same incoming request array
                existingPortfolioMap.put(tickerSymbol, brandNewPortfolio);
                log.info("Created new portfolio item mapping. userId={}, tickerSymbol={}", userId, tickerSymbol);
            }
        }

        // 5. Bulk commit all records down the JDBC driver at once
        List<UserPortfolio> savedPortfolios = userPortfolioRepository.saveAll(entitiesToSave);

        return savedPortfolios.stream()
                .map(this::toResponse)
                .toList();
    }

    private static UserPortfolio mapToUserPortfolioEntity(UUID userId, CreatePortfolioRequest request, StockResponse stock) {
        UserPortfolio portfolio = new UserPortfolio();
        portfolio.setUserId(userId);
        portfolio.setCompanyName(stock.getCompanyName());
        portfolio.setTickerSymbol(stock.getTickerSymbol().toUpperCase());
        portfolio.setQuantity(request.getQuantity());
        portfolio.setBuyingPrice(request.getBuyingPrice());
        return portfolio;
    }

    @Transactional(readOnly = true)
    public List<PortfolioResponse> getMyPortfolio(UUID userId) {
        log.info("Fetching portfolio for userId={}", userId);

        return userPortfolioRepository.findByUserIdOrderByCompanyNameAsc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public boolean deletedMyPortFolio(UUID userId, String stockName) {
        log.info("Deleting stock {}", stockName);
        return userPortfolioRepository.deleteByUserIdAndTickerSymbolIgnoreCase(userId, stockName) > 0;
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
