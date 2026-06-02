-- =============================================================================
-- NutriTrack database changelog
-- =============================================================================
-- Document every schema change in this file, newest section at the bottom.
--
-- Server: MySQL localhost:3306
-- Schema: nutritrackjava
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 2026-05-20 — Schema nutritrackjava on localhost:3306
-- -----------------------------------------------------------------------------

CREATE DATABASE IF NOT EXISTS nutritrackjava
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 2026-05-21 — Authentication tables (JWT auth)
-- Run against schema nutritrackjava before starting Spring Boot (ddl-auto=validate)
-- -----------------------------------------------------------------------------

USE nutritrackjava;

CREATE TABLE IF NOT EXISTS users (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL,
    email           VARCHAR(255) NOT NULL,
    password        VARCHAR(255) NOT NULL,
    role            VARCHAR(20)  NOT NULL DEFAULT 'USER',
    email_verified  BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_users_username UNIQUE (username),
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_username ON users (username);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    token_hash  VARCHAR(255) NOT NULL,
    expires_at  TIMESTAMP    NOT NULL,
    revoked     BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);

-- Add table migrations below.
