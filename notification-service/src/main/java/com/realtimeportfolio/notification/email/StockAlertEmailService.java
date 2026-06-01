package com.realtimeportfolio.notification.email;



import lombok.extern.slf4j.Slf4j;
import com.realtimeportfolio.common.dto.StockAlertMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StockAlertEmailService {

    private final JavaMailSender mailSender;

    public StockAlertEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAlertEmail(StockAlertMessage message) {

        log.info("Preparing stock alert email. userEmail={}, tickerSymbol={}, alertType={}",
                message.getUserEmail(),
                message.getTickerSymbol(),
                message.getAlertType());
        SimpleMailMessage email = new SimpleMailMessage();

        email.setTo(message.getUserEmail());
        email.setSubject("Stock Alert: " + message.getTickerSymbol());

        email.setText(
                "Stock alert triggered\n\n" +
                        "Company: " + message.getCompanyName() + "\n" +
                        "Ticker: " + message.getTickerSymbol() + "\n" +
                        "Alert Type: " + message.getAlertType() + "\n\n" +
                        "Buying Price: " + message.getBuyingPrice() + "\n" +
                        "Current Price: " + message.getCurrentPrice() + "\n" +
                        "Quantity: " + message.getQuantity() + "\n\n" +
                        "Gain/Loss Per Stock: " + message.getGainLossPerStock() + "\n" +
                        "Total Gain/Loss: " + message.getTotalGainLoss()
        );

        mailSender.send(email);
        log.info("Stock alert email sent successfully. userEmail={}, tickerSymbol={}",
                message.getUserEmail(),
                message.getTickerSymbol());
    }
}