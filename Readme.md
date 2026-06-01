Realtime Portfolio Monitoring System
Project Description

The Realtime Portfolio Monitoring System is a microservices-based stock portfolio management application that allows users to register, log in, create or upload stock portfolios, configure price alert thresholds, receive email alerts when thresholds are crossed, and monitor portfolio performance in near real time.

The application is designed using Spring Boot microservices, Spring Cloud Gateway, Eureka Service Discovery, Kafka, RabbitMQ, Redis, and PostgreSQL. It follows clean separation of responsibilities across services and uses asynchronous messaging for real-time stock price processing and alert notification workflows.

Users can manage their stock holdings by manually creating portfolio entries or uploading Excel files. The system validates stock tickers against a stock master service, stores portfolio data, calculates investment value, tracks live price movements, and displays profit/loss calculations through a monitoring dashboard.

Business Capabilities

The system supports the following user stories:

User Story	Description
US1	User registration
US2	User login and JWT token generation
US3	Homepage with user details, menus, permissions, and stock ticker data
US4	Store valid stock names and ticker symbols
US5	Upload portfolio using Excel file
US6	Create portfolio manually
US7	Set upper and lower stock price alert thresholds
US8	Send email alert when stock price crosses threshold
US9	Realtime monitoring of portfolio valuation
High-Level Architecture
                            ┌──────────────────────┐
                            │      Frontend UI      │
                            │  Web / Mobile Client  │
                            └───────────┬──────────┘
                                        │
                                        │ HTTP + JWT
                                        ▼
                            ┌──────────────────────┐
                            │      API Gateway      │
                            │ Spring Cloud Gateway  │
                            │ JWT Validation Filter │
                            └───────────┬──────────┘
                                        │
                    ┌───────────────────┼───────────────────┐
                    │                   │                   │
                    ▼                   ▼                   ▼
        ┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐
        │ authentication   │ │   stock-service  │ │ portfolio-service│
        │    service       │ │                  │ │                  │
        │ Login/Register   │ │ Stock Master     │ │ Portfolio CRUD   │
        │ JWT Generation   │ │ Ticker Validate  │ │ Thresholds       │
        └────────┬─────────┘ └────────┬─────────┘ │ Monitoring       │
                 │                    │           └────────┬─────────┘
                 │                    │                    │
                 ▼                    ▼                    ▼
            ┌─────────┐          ┌─────────┐          ┌─────────┐
            │ auth_db │          │stock_db │          │portfolio│
            └─────────┘          └─────────┘          │   _db   │
                                                       └────┬────┘
                                                            │
                                                            │ Reads latest prices
                                                            ▼
                                                       ┌─────────┐
                                                       │  Redis  │
                                                       │ Latest  │
                                                       │ Prices  │
                                                       └────▲────┘
                                                            │
                                                            │ Consumes Kafka events
                                                            │
                             ┌──────────────────────┐       │
                             │  market-data-service │───────┘
                             │ Yahoo Finance Client │
                             │ Kafka Producer       │
                             └───────────┬──────────┘
                                         │
                                         ▼
                                  ┌─────────────┐
                                  │    Kafka    │
                                  │stock-price- │
                                  │  updates    │
                                  └─────────────┘


Alert Flow:

portfolio-service / alert generator
        │
        ▼
┌────────────────┐
│    RabbitMQ    │
│ stock.alert    │
│    queue       │
└───────┬────────┘
        │
        ▼
┌──────────────────────┐
│ notification-service │
│ Email Alert Consumer │
│ Alert History        │
└──────────┬───────────┘
           ▼
  ┌─────────────────┐
  │ notification_db │
  └─────────────────┘


Service Discovery:

All services register with:

┌──────────────────────┐
│  service-discovery   │
│    Eureka Server     │
└──────────────────────┘