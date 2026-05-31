package com.portfolio.consumer;

import com.portfolio.email.StockAlertEmailService;
import com.portfolio.events.EmailAlertEvent;
import com.portfolio.notification.service.EmailService;
import com.portfolio.rabbitmq.RabbitMQQueues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.portfolio.dto.StockAlertMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailAlertConsumer {

    private final StockAlertEmailService stockAlertEmailService;

    @RabbitListener(queues = RabbitMQQueues.EMAIL_ALERT_QUEUE)
    public void consumeEmailAlert(StockAlertMessage event) {

        log.info("Received email alert event from RabbitMQ: {}", event);

        stockAlertEmailService.sendAlertEmail(event);

        log.info("Email alert processed successfully for userId={}, ticker={}",
                event.getUserEmail(),
                event.getTickerSymbol());
    }
}