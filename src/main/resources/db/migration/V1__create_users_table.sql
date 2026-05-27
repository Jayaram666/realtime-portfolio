CREATE DATABASE investor_db;
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE menus(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    label VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE permissions(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100),
    label VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);

CREATE TABLE menu_permissions(
    id BIGSERIAL PRIMARY KEY,
    menu_id BIGINT NOT NULL,
    permission_id NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_menu_permissions UNIQUE (menu_id, permission_id),
    CONSTRAINT fk_menu_permissions_menu FOREIGN KEY (menu_id) REFERENCES menus(id) ON DELETE CASCADE,
    CONSTRAINT fk_menu_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE);

CREATE TABLE user_menu_permissions(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    menu_permission_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_menu_permissions UNIQUE (user_id, menu_permission_id),
    CONSTRAINT fk_user_menu_permissions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_menu_permissions_menu_permission FOREIGN KEY (menu_permission_id) REFERENCES menu_permissions(id) ON DELETE CASCADE);
