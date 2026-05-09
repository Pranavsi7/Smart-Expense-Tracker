-- Idempotent init for Docker. Runs automatically on first container start.

-- Sequences (lowercase names — PostgreSQL always stores unquoted identifiers lowercase)
CREATE SEQUENCE IF NOT EXISTS et_users_seq        INCREMENT 1 START 1;
CREATE SEQUENCE IF NOT EXISTS et_categories_seq   INCREMENT 1 START 1;
CREATE SEQUENCE IF NOT EXISTS et_transactions_seq INCREMENT 1 START 1000;

-- Users
CREATE TABLE IF NOT EXISTS et_users (
    user_id    INTEGER      PRIMARY KEY,
    first_name VARCHAR(20)  NOT NULL,
    last_name  VARCHAR(20)  NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   TEXT         NOT NULL
);

-- Categories
CREATE TABLE IF NOT EXISTS et_categories (
    category_id INTEGER      PRIMARY KEY,
    user_id     INTEGER      NOT NULL,
    title       VARCHAR(50)  NOT NULL,
    description VARCHAR(100) NOT NULL DEFAULT '',
    CONSTRAINT fk_cat_user FOREIGN KEY (user_id) REFERENCES et_users(user_id)
);

-- Transactions
CREATE TABLE IF NOT EXISTS et_transactions (
    transaction_id   INTEGER        PRIMARY KEY,
    category_id      INTEGER        NOT NULL,
    user_id          INTEGER        NOT NULL,
    amount           NUMERIC(12, 2) NOT NULL,
    note             VARCHAR(100)   NOT NULL DEFAULT '',
    transaction_date BIGINT         NOT NULL,
    CONSTRAINT fk_tx_cat  FOREIGN KEY (category_id) REFERENCES et_categories(category_id),
    CONSTRAINT fk_tx_user FOREIGN KEY (user_id)     REFERENCES et_users(user_id)
);
