package com.portfolio.email;


import org.portfolio.dto.StockAlertMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class StockAlertEmailService {

    private final JavaMailSender mailSender;

    public StockAlertEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAlertEmail(StockAlertMessage message) {

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
    }
}