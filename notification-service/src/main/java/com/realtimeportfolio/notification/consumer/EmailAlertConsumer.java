package com.realtimeportfolio.notification.consumer;

import com.realtimeportfolio.notification.email.StockAlertEmailService;
import com.realtimeportfolio.common.config.rabbitmq.RabbitMQQueues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.common.dto.StockAlertMessage;
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