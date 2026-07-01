package com.realtimeportfolio.portfolio.scheduler;


import com.realtimeportfolio.portfolio.entity.StockAlertHistory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.portfolio.entity.PortfolioAlertThreshold;
import com.realtimeportfolio.portfolio.entity.UserPortfolio;
import com.realtimeportfolio.common.config.rabbitmq.RabbitMQExchanges;
import com.realtimeportfolio.common.config.rabbitmq.RabbitMQRoutingKeys;
import com.realtimeportfolio.portfolio.repo.PortfolioAlertThresholdRepository;
import com.realtimeportfolio.portfolio.repo.StockAlertHistoryRepository;
import com.realtimeportfolio.portfolio.repo.UserPortfolioRepository;
import com.realtimeportfolio.portfolio.service.StackMarketService;
import com.realtimeportfolio.common.dto.StockAlertMessage;
import com.realtimeportfolio.common.dto.StockTickerDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class StockAlertGenerator {

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

    @Scheduled(fixedRateString = "${portfolio.alerts.fixed-rate-ms:60000}")
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
//        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);

        StockAlertHistory stockAlertHistory = alertHistoryRepository.findById(threshold.getUserId()).get();
        boolean alreadySent = stockAlertHistory.isEmailSent() && stockAlertHistory.getAlertType().equals(alertType);
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


        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String emailFromHeader = null;

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            // 2. Extract the header injected by your gateway
            emailFromHeader = request.getHeader("X-User-Email");
        }
        StockAlertMessage message = new StockAlertMessage(
                threshold.getUserId(),
                emailFromHeader,
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
        stockAlertHistory.setEmailSent(true);
        alertHistoryRepository.save(stockAlertHistory);

        log.info("Stock alert message published. userId={}, tickerSymbol={}, alertType={}",
                threshold.getUserId(),
                threshold.getTickerSymbol(),
                alertType);
    }
}