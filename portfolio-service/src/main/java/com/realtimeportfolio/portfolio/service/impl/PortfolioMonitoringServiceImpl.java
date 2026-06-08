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
import java.util.Optional;

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
    public PortfolioMonitoringResponse getRealtimePortfolio(Long userId) {

        log.info("US9 realtime monitoring started. userId={}", userId);

        List<UserPortfolio> portfolios =
                userPortfolioRepository.findByUserIdOrderByCompanyNameAsc(userId);

        List<PortfolioStockValuationResponse> stockResponses =
                portfolios.stream()
                        .map(portfolio -> calculateStockValuation(userId, portfolio))
                        .toList();

        BigDecimal totalInvestedValue = stockResponses.stream()
                .map(PortfolioStockValuationResponse::getInvestedValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCurrentValue = stockResponses.stream()
                .map(PortfolioStockValuationResponse::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGainLossAmount =
                totalCurrentValue.subtract(totalInvestedValue);

        BigDecimal totalGainLossPercent =
                calculatePercentage(totalGainLossAmount, totalInvestedValue);

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
            Long userId,
            UserPortfolio portfolio
    ) {
        log.info("Calculating stock valuation for userId={}, ticker={}", userId, portfolio.getTickerSymbol());
        Optional<StockPriceUpdateEvent> latestPriceOptional =
                redisStockPriceStore.getLatestPrice(portfolio.getTickerSymbol());

        boolean currentPriceAvailable = latestPriceOptional.isPresent();

        BigDecimal currentPrice = latestPriceOptional
                .map(StockPriceUpdateEvent::getCurrentPrice)
                .orElse(portfolio.getBuyingPrice());

        LocalDateTime priceLastUpdatedAt = latestPriceOptional
                .map(StockPriceUpdateEvent::getEventTime)
                .orElse(null);

        BigDecimal quantity =
                BigDecimal.valueOf(portfolio.getQuantity());

        BigDecimal investedValue =
                portfolio.getBuyingPrice().multiply(quantity);

        BigDecimal currentValue =
                currentPrice.multiply(quantity);

        BigDecimal gainLossAmount =
                currentValue.subtract(investedValue);

        BigDecimal gainLossPercent =
                calculatePercentage(gainLossAmount, investedValue);

        ThresholdStatus thresholdStatus =
                calculateThresholdStatus(
                        userId,
                        portfolio.getTickerSymbol(),
                        currentPrice,
                        currentPriceAvailable
                );

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

    private ThresholdStatus calculateThresholdStatus(
            Long userId,
            String tickerSymbol,
            BigDecimal currentPrice,
            boolean currentPriceAvailable
    ) {
        if (!currentPriceAvailable) {
            return new ThresholdStatus(false, false);
        }

        Optional<PortfolioAlertThreshold> thresholdOptional =
                thresholdRepository.findByUserIdAndTickerSymbolIgnoreCase(
                        userId,
                        tickerSymbol
                );

        if (thresholdOptional.isEmpty()) {
            return new ThresholdStatus(false, false);
        }

        PortfolioAlertThreshold threshold = thresholdOptional.get();

        boolean upperCrossed =
                currentPrice.compareTo(threshold.getUpperAlertPrice()) >= 0;

        boolean lowerCrossed =
                currentPrice.compareTo(threshold.getLowerAlertPrice()) <= 0;

        return new ThresholdStatus(upperCrossed, lowerCrossed);
    }

    private BigDecimal calculatePercentage(
            BigDecimal gainLossAmount,
            BigDecimal baseAmount
    ) {
        if (baseAmount == null || baseAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return gainLossAmount
                .multiply(BigDecimal.valueOf(100))
                .divide(baseAmount, 2, RoundingMode.HALF_UP);
    }

    private record ThresholdStatus(
            boolean upperThresholdCrossed,
            boolean lowerThresholdCrossed
    ) {
    }
}