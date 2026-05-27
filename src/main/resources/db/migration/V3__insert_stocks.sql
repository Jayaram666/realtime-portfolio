CREATE TABLE stocks (
    id BIGSERIAL PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    ticker_symbol VARCHAR(50) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO stocks (company_name, ticker_symbol, active)
VALUES
('Reliance Industries Limited', 'RELIANCE', true),
('Tata Consultancy Services Limited', 'TCS', true),
('HDFC Bank Limited', 'HDFCBANK', true),
('Infosys Limited', 'INFY', true),
('ICICI Bank Limited', 'ICICIBANK', true),
('State Bank of India', 'SBIN', true),
('Bharti Airtel Limited', 'BHARTIARTL', true),
('Larsen & Toubro Limited', 'LT', true),
('Axis Bank Limited', 'AXISBANK', true),
('Kotak Mahindra Bank Limited', 'KOTAKBANK', true);