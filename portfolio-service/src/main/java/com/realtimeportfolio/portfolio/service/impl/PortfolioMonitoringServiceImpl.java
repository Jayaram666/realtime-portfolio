package com.realtimeportfolio.portfolio.service.impl;

import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.portfolio.cache.RedisStockPriceStore;
import com.realtimeportfolio.portfolio.entity.PortfolioAlertThreshold;
import com.realtimeportfolio.portfolio.entity.UserPortfolio;
import com.realtimeportfolio.portfolio.repo.PortfolioAlertThresholdRepository;
import com.realtimeportfolio.portfolio.repo.UserPortfolioRepository;
import com.realtimeportfolio.portfolio.service.PortfolioMonitoringService;
import com.realtimeportfolio.common.dto.PortfolioMonitoringResponse;
import com.realtimeportfolio.common.dto.PortfolioStockValuationResponse;
import com.realtimeportfolio.common.dto.StockPriceUpdateEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PortfolioMonitoringServiceImpl implements PortfolioMonitoringService {

    private final UserPortfolioRepository userPortfolioRepository;
    private final PortfolioAlertThresholdRepository thresholdRepository;
    private final RedisStockPriceStore redisStockPriceStore;

    public PortfolioMonitoringServiceImpl(
            UserPortfolioRepository userPortfolioRepository,
            PortfolioAlertThresholdRepository thresholdRepository,
            RedisStockPriceStore redisStockPriceStore
    ) {
        this.userPortfolioRepository = userPortfolioRepository;
        this.thresholdRepository = thresholdRepository;
        this.redisStockPriceStore = redisStockPriceStore;
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioMonitoringResponse getRealtimePortfolio(UUID userId) {
        log.debug("Realtime portfolio valuation processing started for userId={}", userId);

        // 1. Fetch user assets in one trip
        List<UserPortfolio> portfolios = userPortfolioRepository.findByUserIdOrderByCompanyNameAsc(userId);
        if (portfolios.isEmpty()) {
            return new PortfolioMonitoringResponse(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, LocalDateTime.now(), List.of());
        }

        // 2. Optimization: Bulk fetch ALL alert thresholds for this user in ONE query to completely fix the N+1 loop leak
        List<PortfolioAlertThreshold> userThresholds = thresholdRepository.findByUserId(userId);
        Map<String, PortfolioAlertThreshold> thresholdMap = userThresholds.stream()
                .collect(Collectors.toMap(
                        t -> t.getTickerSymbol().toUpperCase(),
                        t -> t,
                        (existing, replacement) -> existing
                ));

        List<PortfolioStockValuationResponse> stockResponses = portfolios.stream()
                .map(portfolio -> calculateStockValuation(portfolio, thresholdMap))
                .toList();

        // 4. Compute global aggregated mathematical thresholds
        BigDecimal totalInvestedValue = stockResponses.stream()
                .map(PortfolioStockValuationResponse::getInvestedValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCurrentValue = stockResponses.stream()
                .map(PortfolioStockValuationResponse::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGainLossAmount = totalCurrentValue.subtract(totalInvestedValue);
        BigDecimal totalGainLossPercent = calculatePercentage(totalGainLossAmount, totalInvestedValue);

        // 5. Construct response. Stock responses are intentionally placed first in the constructor parameters
        return new PortfolioMonitoringResponse(
                totalInvestedValue,
                totalCurrentValue,
                totalGainLossAmount,
                totalGainLossPercent,
                LocalDateTime.now(),
                stockResponses
        );
    }

    private PortfolioStockValuationResponse calculateStockValuation(
            UserPortfolio portfolio,
            Map<String, PortfolioAlertThreshold> thresholdMap
    ) {
        String ticker = portfolio.getTickerSymbol().toUpperCase();

        Optional<StockPriceUpdateEvent> latestPriceOptional = redisStockPriceStore.getLatestPrice(ticker);
        boolean currentPriceAvailable = latestPriceOptional.isPresent();

        BigDecimal currentPrice = latestPriceOptional
                .map(StockPriceUpdateEvent::getCurrentPrice)
                .orElse(portfolio.getBuyingPrice());

        LocalDateTime priceLastUpdatedAt = latestPriceOptional
                .map(StockPriceUpdateEvent::getEventTime)
                .orElse(null);

        BigDecimal quantity = BigDecimal.valueOf(portfolio.getQuantity());
        BigDecimal investedValue = portfolio.getBuyingPrice().multiply(quantity);
        BigDecimal currentValue = currentPrice.multiply(quantity);

        BigDecimal gainLossAmount = currentValue.subtract(investedValue);
        BigDecimal gainLossPercent = calculatePercentage(gainLossAmount, investedValue);

        // Instant O(1) in-memory checking using the extracted map
        ThresholdStatus thresholdStatus = evaluateThreshold(ticker, currentPrice, currentPriceAvailable, thresholdMap);

        return new PortfolioStockValuationResponse(
                portfolio.getCompanyName(),
                portfolio.getTickerSymbol(),
                portfolio.getQuantity(),
                portfolio.getBuyingPrice(),
                currentPrice,
                investedValue,
                currentValue,
                gainLossAmount,
                gainLossPercent,
                thresholdStatus.upperThresholdCrossed(),
                thresholdStatus.lowerThresholdCrossed(),
                currentPriceAvailable,
                priceLastUpdatedAt
        );
    }

    private ThresholdStatus evaluateThreshold(
            String tickerSymbol,
            BigDecimal currentPrice,
            boolean currentPriceAvailable,
            Map<String, PortfolioAlertThreshold> thresholdMap
    ) {
        if (!currentPriceAvailable || !thresholdMap.containsKey(tickerSymbol)) {
            return new ThresholdStatus(false, false);
        }

        PortfolioAlertThreshold threshold = thresholdMap.get(tickerSymbol);
        boolean upperCrossed = currentPrice.compareTo(threshold.getUpperAlertPrice()) >= 0;
        boolean lowerCrossed = currentPrice.compareTo(threshold.getLowerAlertPrice()) <= 0;

        return new ThresholdStatus(upperCrossed, lowerCrossed);
    }

    private BigDecimal calculatePercentage(BigDecimal gainLossAmount, BigDecimal baseAmount) {
        if (baseAmount == null || baseAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return gainLossAmount
                .multiply(BigDecimal.valueOf(100))
                .divide(baseAmount, 2, RoundingMode.HALF_UP);
    }

    private record ThresholdStatus(boolean upperThresholdCrossed, boolean lowerThresholdCrossed) {
    }
}
