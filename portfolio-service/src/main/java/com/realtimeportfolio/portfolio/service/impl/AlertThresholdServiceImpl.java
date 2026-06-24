package com.realtimeportfolio.portfolio.service.impl;



import com.realtimeportfolio.portfolio.entity.PortfolioAlertThreshold;
import com.realtimeportfolio.portfolio.entity.UserPortfolio;
import com.realtimeportfolio.portfolio.repo.PortfolioAlertThresholdRepository;
import com.realtimeportfolio.portfolio.repo.UserPortfolioRepository;
import com.realtimeportfolio.portfolio.service.AlertThresholdService;
import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.common.dto.AlertThresholdRequest;
import com.realtimeportfolio.common.dto.AlertThresholdResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class AlertThresholdServiceImpl implements AlertThresholdService {


    private final UserPortfolioRepository userPortfolioRepository;
    private final PortfolioAlertThresholdRepository thresholdRepository;

    public AlertThresholdServiceImpl(
            UserPortfolioRepository userPortfolioRepository,
            PortfolioAlertThresholdRepository thresholdRepository
    ) {
        this.userPortfolioRepository = userPortfolioRepository;
        this.thresholdRepository = thresholdRepository;
    }

    @Transactional
    @Override
    public AlertThresholdResponse createOrUpdateThreshold(
            UUID userId,
            AlertThresholdRequest request
    ) {
        String tickerSymbol = request.getTickerSymbol().trim().toUpperCase();

        log.info("Alert threshold create/update request received. userId={}, tickerSymbol={}",
                userId,
                tickerSymbol
        );

        validateThresholdPercent(request.getUpperThresholdPercent(), request.getLowerThresholdPercent());

        UserPortfolio portfolio = userPortfolioRepository
                .findByUserIdAndTickerSymbolIgnoreCase(userId, tickerSymbol)
                .orElseThrow(() -> new RuntimeException(
                        "Cannot set threshold. Stock is not available in user portfolio: " + tickerSymbol
                ));

        PortfolioAlertThreshold threshold = thresholdRepository
                .findByUserIdAndTickerSymbolIgnoreCase(userId, tickerSymbol)
                .orElseGet(() -> {
                    PortfolioAlertThreshold newThreshold = new PortfolioAlertThreshold();
                    newThreshold.setUserId(userId);
                    newThreshold.setTickerSymbol(tickerSymbol);
                    newThreshold.setActive(true);
                    return newThreshold;
                });

        threshold.updateThresholds(
                request.getUpperThresholdPercent(),
                request.getLowerThresholdPercent(),
                portfolio.getBuyingPrice()
        );

        PortfolioAlertThreshold saved = thresholdRepository.save(threshold);

        log.info("Alert threshold saved successfully. userId={}, tickerSymbol={}, upper={}%, lower={}%",
                userId,
                tickerSymbol,
                saved.getUpperThresholdPercent(),
                saved.getLowerThresholdPercent()
        );

        return toResponse(saved, portfolio.getBuyingPrice());
    }

    @Transactional(readOnly = true)
    @Override
    public List<AlertThresholdResponse> getMyThresholds(UUID userId) {
        log.info("Fetching alert thresholds for userId={}", userId);

        return thresholdRepository.findByUserIdAndActiveTrueOrderByTickerSymbolAsc(userId)
                .stream()
                .map(threshold -> {
                    UserPortfolio portfolio = userPortfolioRepository
                            .findByUserIdAndTickerSymbolIgnoreCase(userId, threshold.getTickerSymbol())
                            .orElseThrow(() -> new RuntimeException(
                                    "Portfolio not found for ticker: " + threshold.getTickerSymbol()
                            ));

                    return toResponse(threshold, portfolio.getBuyingPrice());
                })
                .toList();
    }

    private void validateThresholdPercent(
            BigDecimal upperThresholdPercent,
            BigDecimal lowerThresholdPercent
    ) {
        if (upperThresholdPercent.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Upper threshold percent must be greater than zero");
        }

        if (lowerThresholdPercent.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Lower threshold percent must be greater than zero");
        }

        if (upperThresholdPercent.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new RuntimeException("Upper threshold percent cannot be greater than 100");
        }

        if (lowerThresholdPercent.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new RuntimeException("Lower threshold percent cannot be greater than 100");
        }
    }

    private AlertThresholdResponse toResponse(
            PortfolioAlertThreshold threshold,
            BigDecimal buyingPrice
    ) {
        return new AlertThresholdResponse(
                threshold.getId(),
                threshold.getTickerSymbol(),
                buyingPrice,
                threshold.getUpperThresholdPercent(),
                threshold.getLowerThresholdPercent(),
                threshold.getUpperAlertPrice(),
                threshold.getLowerAlertPrice(),
                threshold.isActive()
        );
    }
}