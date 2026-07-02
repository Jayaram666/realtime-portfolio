SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

-- =========================================================================
-- 1. TABLE CREATION (SCHEMA)
-- =========================================================================
create database portfolio_db;

CREATE TABLE public.menu_permission (
    id uuid NOT NULL,
    menu_id uuid NOT NULL,
    permission_id uuid NOT NULL
);

CREATE TABLE public.menus (
    id uuid NOT NULL,
    active boolean NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    display_order integer NOT NULL,
    label character varying(255) NOT NULL,
    name character varying(255) NOT NULL
);

CREATE TABLE public.permissions (
    id uuid NOT NULL,
    code character varying(255) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    name character varying(255) NOT NULL
);

CREATE TABLE public.portfolio_alert_thresholds (
    id uuid NOT NULL,
    active boolean NOT NULL,
    created_at timestamp(6) without time zone,
    lower_alert_price numeric(19,4) NOT NULL,
    lower_threshold_percent numeric(5,2) NOT NULL,
    ticker_symbol character varying(255) NOT NULL,
    updated_at timestamp(6) without time zone,
    upper_alert_price numeric(19,4) NOT NULL,
    upper_threshold_percent numeric(5,2) NOT NULL,
    user_id uuid NOT NULL
);

CREATE TABLE public.stacks (
    id uuid NOT NULL,
    active boolean NOT NULL,
    company_name character varying(255) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    ticker_symbol character varying(255) NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL
);

CREATE TABLE public.stock_alert_history (
    id uuid NOT NULL,
    alert_type character varying(255),
    buying_price numeric(38,2),
    created_at timestamp(6) without time zone,
    current_price numeric(38,2),
    email_sent boolean NOT NULL,
    gain_loss_per_stock numeric(38,2),
    ticker_symbol character varying(255),
    total_gain_loss numeric(38,2),
    user_id uuid
);

CREATE TABLE public.user_menu_permission (
    id uuid NOT NULL,
    menu_permission_id uuid NOT NULL,
    user_id uuid NOT NULL
);

CREATE TABLE public.user_portfolios (
    id uuid NOT NULL,
    buying_price numeric(38,2) NOT NULL,
    company_name character varying(255) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    quantity integer NOT NULL,
    ticker_symbol character varying(255) NOT NULL,
    total_invested_amount numeric(38,2) NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    user_id uuid NOT NULL
);

CREATE TABLE public.users (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    email character varying(255) NOT NULL,
    name character varying(150) NOT NULL
);

-- =========================================================================
-- 2. DATA INSERTIONS (SEED DATA)
-- =========================================================================

-- Data for public.menu_permission
INSERT INTO public.menu_permission (id, menu_id, permission_id) VALUES
('99999999-1111-a1b2-c3d4-e5f67a8b9c0d', 'a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d', '11111111-2222-3333-4444-555555555555'),
('99999999-2222-a1b2-c3d4-e5f67a8b9c0d', 'a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d', '22222222-3333-4444-5555-666666666666'),
('99999999-3333-a1b2-c3d4-e5f67a8b9c0d', 'a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d', '33333333-4444-5555-6666-777777777777'),
('99999999-4444-a1b2-c3d4-e5f67a8b9c0d', 'a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d', '44444444-5555-6666-7777-888888888888');

-- Data for public.menus
INSERT INTO public.menus (id, active, created_at, display_order, label, name) VALUES
('a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d', true, '2026-06-24 19:26:05.98251', 1, 'Portfolio Management', 'PortfolioManagement'),
('f5e4d3c2-b1a0-9f8e-7d6c-5b4a3f2e1d0c', true, '2026-06-24 19:26:05.98251', 2, 'User Management', 'UserManagement');

-- Data for public.permissions
INSERT INTO public.permissions (id, code, created_at, name) VALUES
('11111111-2222-3333-4444-555555555555', 'PORTFOLIO_CREATE', '2026-06-24 19:26:05.98251', 'Create Portfolio'),
('22222222-3333-4444-5555-666666666666', 'PORTFOLIO_READ', '2026-06-24 19:26:05.98251', 'View Portfolio'),
('33333333-4444-5555-6666-777777777777', 'PORTFOLIO_UPDATE', '2026-06-24 19:26:05.98251', 'Update Portfolio'),
('44444444-5555-6666-7777-888888888888', 'PORTFOLIO_DELETE', '2026-06-24 19:26:05.98251', 'Delete Portfolio');

-- Data for public.portfolio_alert_thresholds
INSERT INTO public.portfolio_alert_thresholds (id, active, created_at, lower_alert_price, lower_threshold_percent, ticker_symbol, updated_at, upper_alert_price, upper_threshold_percent, user_id) VALUES
('de2cc140-7c00-491c-b256-410352e923c6', true, '2026-06-25 09:49:25.299767', 2.8500, 5.00, 'ICICIBANK', '2026-06-25 09:49:25.299777', 3.3000, 10.00, '68da7359-81ac-4d40-a287-e7de29a94f36'),
('b5f3ab9e-3e52-44b1-bf50-2c8e31de83aa', true, '2026-06-25 18:24:17.338717', 11.8800, 1.00, 'ICICIBANK', '2026-06-25 18:24:17.338746', 12.6000, 5.00, '912b8475-19bc-4ff1-a738-606938c4f876'),
('83c5748e-d87e-4962-95ef-e92aa9e6af66', true, '2026-06-25 18:42:05.902933', 1.9000, 5.00, 'MRF', '2026-06-25 18:42:05.903037', 2.2000, 10.00, '912b8475-19bc-4ff1-a738-606938c4f876'),
('b9dffa14-5161-43a7-9fc7-dc1d08394869', true, '2026-06-26 07:00:24.303448', 126959.0000, 2.00, 'MRF', '2026-06-26 07:00:44.825478', 130845.5000, 1.00, '2d91c71b-c391-3076-a5b0-2aa5fb061db6'),
('acbacd94-0580-45b1-a32f-7d0c073ecc6f', true, '2026-06-26 07:27:51.364279', 3564.0000, 1.00, 'TCS', '2026-06-26 07:29:59.064976', 3636.0000, 1.00, '912b8475-19bc-4ff1-a738-606938c4f876'),
('06f89e25-f693-401b-a031-6fd8dee55ed3', true, '2026-06-26 08:38:27.168927', 1113.7500, 1.00, 'ICICIBANK', '2026-06-26 08:38:27.168963', 1136.2500, 1.00, '818b9166-fbd9-466c-8d4e-4652b9957020'),
('089dff53-63c0-41da-bf92-0664f3f34352', true, '2026-06-26 08:52:59.709602', 99.0000, 1.00, 'MRF', '2026-06-26 08:52:59.709737', 101.0000, 1.00, '818b9166-fbd9-466c-8d4e-4652b9957020'),
('15a008c4-d5f2-478d-bc8b-0de225c36852', true, '2026-06-26 10:04:37.079789', 2.2275, 1.00, 'ICICIBANK', '2026-06-26 10:04:37.07983', 2.2725, 1.00, '4066e02d-b681-4cbc-9769-0eaa84346114');
