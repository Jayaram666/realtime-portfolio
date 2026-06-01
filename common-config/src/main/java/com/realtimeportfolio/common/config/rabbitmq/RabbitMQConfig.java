package com.realtimeportfolio.common.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableRabbit
@Configuration
@ConditionalOnClass(Queue.class)
@ConditionalOnProperty(name = "spring.rabbitmq.host")
public class RabbitMQConfig {

    @Bean
    public Queue emailAlertQueue() {
        return new Queue(RabbitMQQueues.EMAIL_ALERT_QUEUE, true);
    }

    @Bean
    public DirectExchange alertExchange() {
        return new DirectExchange(RabbitMQExchanges.ALERT_EXCHANGE);
    }

    @Bean
    public Binding emailAlertBinding() {
        return BindingBuilder
                .bind(emailAlertQueue())
                .to(alertExchange())
                .with(RabbitMQRoutingKeys.EMAIL_ALERT_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}