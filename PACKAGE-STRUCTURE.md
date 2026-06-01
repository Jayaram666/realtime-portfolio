# Package Structure

This project now uses readable, module-specific Java base packages instead of the short shared `com.portfolio` / `org.portfolio` packages.

## Application packages

| Module | Java base package |
| --- | --- |
| api-gateway | `com.realtimeportfolio.gateway` |
| service-discovery | `com.realtimeportfolio.discovery` |
| authentication-service | `com.realtimeportfolio.authentication` |
| stack-service | `com.realtimeportfolio.stock` |
| market-data-service | `com.realtimeportfolio.marketdata` |
| portfolio-service | `com.realtimeportfolio.portfolio` |
| notification-service | `com.realtimeportfolio.notification` |
| common-lib | `com.realtimeportfolio.common` |
| common-config | `com.realtimeportfolio.common.config` |

## Shared packages

| Purpose | Package |
| --- | --- |
| Shared DTOs | `com.realtimeportfolio.common.dto` |
| Shared exception handling | `com.realtimeportfolio.common.exception` |
| Kafka configuration/constants | `com.realtimeportfolio.common.config.kafka` |
| RabbitMQ configuration/constants | `com.realtimeportfolio.common.config.rabbitmq` |
| Redis configuration/constants | `com.realtimeportfolio.common.config.redis` |

## Spring bootstrapping

Each application now scans only its own module package using `@SpringBootApplication(scanBasePackages = "...")`.

Shared infrastructure is imported explicitly using `@Import(...)`:

- `authentication-service`: imports `GlobalExceptionHandler`.
- `stack-service`: imports `GlobalExceptionHandler`.
- `market-data-service`: imports `GlobalExceptionHandler`, Kafka producer/topic config, and Redis config.
- `portfolio-service`: imports `GlobalExceptionHandler`, Kafka consumer/topic config, and RabbitMQ config.
- `notification-service`: imports `GlobalExceptionHandler` and RabbitMQ config.

This avoids accidental component scanning across unrelated microservices while still allowing shared DTOs, exception handling, and infrastructure configuration.
