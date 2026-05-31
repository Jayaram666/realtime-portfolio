package com.portfolio.scheduler;


import com.portfolio.entity.PortfolioAlertThreshold;
import com.portfolio.entity.UserPortfolio;
import com.portfolio.rabbitmq.RabbitMQConfig;
import com.portfolio.rabbitmq.RabbitMQExchanges;
import com.portfolio.rabbitmq.RabbitMQRoutingKeys;
import com.portfolio.repo.PortfolioAlertThresholdRepository;
import com.portfolio.repo.StockAlertHistoryRepository;
import com.portfolio.repo.UserPortfolioRepository;
import com.portfolio.service.StackMarketService;
import org.portfolio.dto.StockAlertMessage;
import org.portfolio.dto.StockTickerDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockAlertGenerator {

    private static final Logger log = LoggerFactory.getLogger(StockAlertGenerator.class);

    private final PortfolioAlertThresholdRepository thresholdRepository;
    private final UserPortfolioRepository userPortfolioRepository;
    private final StockAlertHistoryRepository alertHistoryRepository;
    private final StackMarketService stackMarketService;
    private final RabbitTemplate rabbitTemplate;

    public StockAlertGenerator(
            PortfolioAlertThresholdRepository thresholdRepository,
            UserPortfolioRepository userPortfolioRepository,
            StockAlertHistoryRepository alertHistoryRepository,
            StackMarketService stackMarketService,
            RabbitTemplate rabbitTemplate
    ) {
        this.thresholdRepository = thresholdRepository;
        this.userPortfolioRepository = userPortfolioRepository;
        this.alertHistoryRepository = alertHistoryRepository;
        this.stackMarketService = stackMarketService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedRate = 60000)
    public void monitorStockPrices() {

        log.info("Stock alert monitoring started");

        List<PortfolioAlertThreshold> thresholds =
                thresholdRepository.findAll();

        for (PortfolioAlertThreshold threshold : thresholds) {
            try {
                processThreshold(threshold);
            } catch (Exception ex) {
                log.error("Error while processing threshold. userId={}, tickerSymbol={}",
                        threshold.getUserId(),
                        threshold.getTickerSymbol(),
                        ex
                );
            }
        }

        log.info("Stock alert monitoring completed");
    }

    private void processThreshold(PortfolioAlertThreshold threshold) {

        if (!threshold.isActive()) {
            return;
        }

        UserPortfolio portfolio = userPortfolioRepository
                .findByUserIdAndTickerSymbolIgnoreCase(
                        threshold.getUserId(),
                        threshold.getTickerSymbol()
                )
                .orElseThrow(() -> new RuntimeException(
                        "Portfolio not found for ticker: " + threshold.getTickerSymbol()
                ));

        BigDecimal currentPrice =
                stackMarketService.getStockPrice(threshold.getTickerSymbol()).
                        map(StockTickerDto::getCurrentPrice)
                        .orElse(BigDecimal.ZERO);

        if (currentPrice.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Current price not available for ticker={}",
                    threshold.getTickerSymbol());
            return;
        }

        if (currentPrice.compareTo(threshold.getUpperAlertPrice()) >= 0) {
            publishAlertIfNotAlreadySent(
                    threshold,
                    portfolio,
                    currentPrice,
                    "UPPER_THRESHOLD_CROSSED"
            );
        }

        if (currentPrice.compareTo(threshold.getLowerAlertPrice()) <= 0) {
            publishAlertIfNotAlreadySent(
                    threshold,
                    portfolio,
                    currentPrice,
                    "LOWER_THRESHOLD_CROSSED"
            );
        }
    }

    private void publishAlertIfNotAlreadySent(
            PortfolioAlertThreshold threshold,
            UserPortfolio portfolio,
            BigDecimal currentPrice,
            String alertType
    ) {
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);

        boolean alreadySent =
                alertHistoryRepository.existsByUserIdAndTickerSymbolAndAlertTypeAndCreatedAtAfter(
                        threshold.getUserId(),
                        threshold.getTickerSymbol(),
                        alertType,
                        last24Hours
                );

        if (alreadySent) {
            log.info("Alert already sent recently. userId={}, tickerSymbol={}, alertType={}",
                    threshold.getUserId(),
                    threshold.getTickerSymbol(),
                    alertType);
            return;
        }

        BigDecimal gainLossPerStock =
                currentPrice.subtract(portfolio.getBuyingPrice());

        BigDecimal totalGainLoss =
                gainLossPerStock.multiply(BigDecimal.valueOf(portfolio.getQuantity()));

        StockAlertMessage message = new StockAlertMessage(
                threshold.getUserId(),
                "user@example.com", // later fetch from User table
                portfolio.getTickerSymbol(),
                portfolio.getCompanyName(),
                portfolio.getQuantity(),
                portfolio.getBuyingPrice(),
                currentPrice,
                gainLossPerStock,
                totalGainLoss,
                alertType
        );

        rabbitTemplate.convertAndSend(
                RabbitMQExchanges.ALERT_EXCHANGE,
                RabbitMQRoutingKeys.EMAIL_ALERT_ROUTING_KEY,
                message
        );

        log.info("Stock alert message published. userId={}, tickerSymbol={}, alertType={}",
                threshold.getUserId(),
                threshold.getTickerSymbol(),
                alertType);
    }
}