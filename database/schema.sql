-- PriceHub Database Schema
-- Database Name: pricehub_db

CREATE DATABASE IF NOT EXISTS pricehub_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pricehub_db;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
    account_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    reset_password_token VARCHAR(100),
    reset_password_token_expiry TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_email (email)
);

-- 2. Marketplaces Table
CREATE TABLE IF NOT EXISTS marketplaces (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    base_url VARCHAR(255),
    logo_url VARCHAR(255),
    trust_score DOUBLE DEFAULT 4.8,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- 3. Products Table
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    brand VARCHAR(100) NOT NULL,
    model_number VARCHAR(100),
    category VARCHAR(50) NOT NULL,
    base_price DOUBLE NOT NULL,
    image_url VARCHAR(500),
    description TEXT,
    rating_average DOUBLE DEFAULT 4.5,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_prod_category (category),
    INDEX idx_prod_brand (brand)
);

-- 4. Product Specifications Table
CREATE TABLE IF NOT EXISTS product_specifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    spec_key VARCHAR(100) NOT NULL,
    spec_value VARCHAR(255) NOT NULL,
    spec_group VARCHAR(100),
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_spec_product (product_id)
);

-- 5. Marketplace Listings Table
CREATE TABLE IF NOT EXISTS marketplace_listings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    marketplace_id BIGINT NOT NULL,
    external_listing_id VARCHAR(100),
    title VARCHAR(255) NOT NULL,
    listed_price DOUBLE NOT NULL,
    delivery_charge DOUBLE NOT NULL DEFAULT 0.0,
    mandatory_charges DOUBLE NOT NULL DEFAULT 0.0,
    rating DOUBLE DEFAULT 4.5,
    review_count INT DEFAULT 0,
    warranty_months INT DEFAULT 12,
    delivery_estimate_days INT DEFAULT 2,
    stock_status VARCHAR(50) DEFAULT 'IN_STOCK',
    is_verified BOOLEAN DEFAULT TRUE,
    product_url VARCHAR(500),
    data_source VARCHAR(50) DEFAULT 'DEMO_DATA',
    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (marketplace_id) REFERENCES marketplaces(id) ON DELETE CASCADE,
    INDEX idx_listing_product (product_id),
    INDEX idx_listing_price (listed_price)
);

-- 6. Offers Table
CREATE TABLE IF NOT EXISTS offers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    listing_id BIGINT NOT NULL,
    offer_type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    discount_amount DOUBLE DEFAULT 0.0,
    discount_percentage DOUBLE DEFAULT 0.0,
    min_purchase_amount DOUBLE DEFAULT 0.0,
    bank_name VARCHAR(100),
    card_type VARCHAR(50) DEFAULT 'ANY',
    coupon_code VARCHAR(50),
    terms_conditions VARCHAR(255),
    is_verified BOOLEAN DEFAULT TRUE,
    valid_until TIMESTAMP NULL,
    FOREIGN KEY (listing_id) REFERENCES marketplace_listings(id) ON DELETE CASCADE,
    INDEX idx_offer_listing (listing_id)
);

-- 7. Price History Table
CREATE TABLE IF NOT EXISTS price_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    listing_id BIGINT NOT NULL,
    observed_price DOUBLE NOT NULL,
    recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_demo_data BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (listing_id) REFERENCES marketplace_listings(id) ON DELETE CASCADE,
    INDEX idx_price_history_listing (listing_id, recorded_at)
);

-- 8. User Preferences Table
CREATE TABLE IF NOT EXISTS user_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    preferred_category VARCHAR(50) DEFAULT 'Smartphones',
    min_budget DOUBLE DEFAULT 10000.0,
    max_budget DOUBLE DEFAULT 80000.0,
    preferred_brand VARCHAR(100) DEFAULT 'Any',
    preferred_marketplace VARCHAR(50) DEFAULT 'ALL',
    max_delivery_days INT DEFAULT 3,
    default_sort VARCHAR(50) DEFAULT 'BEST_VALUE',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 9. User Shopping Requirements (Personalized Deal Lab saved queries)
CREATE TABLE IF NOT EXISTS user_shopping_requirements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    category VARCHAR(50) NOT NULL,
    max_budget DOUBLE,
    required_ram VARCHAR(50),
    required_storage VARCHAR(50),
    preferred_brand VARCHAR(100),
    min_rating DOUBLE,
    min_warranty_months INT,
    max_delivery_days INT,
    preferred_marketplace VARCHAR(50),
    payment_card_type VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 11. Saved Products (Wishlist)
CREATE TABLE IF NOT EXISTS saved_products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    notes VARCHAR(255),
    saved_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_product (user_id, product_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- 12. Price Alerts
CREATE TABLE IF NOT EXISTS price_alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    listing_id BIGINT NOT NULL,
    target_price DOUBLE NOT NULL,
    current_price_at_creation DOUBLE NOT NULL,
    is_triggered BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    triggered_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (listing_id) REFERENCES marketplace_listings(id) ON DELETE CASCADE
);

-- 13. Comparison History
CREATE TABLE IF NOT EXISTS comparison_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_ids VARCHAR(255) NOT NULL,
    summary_title VARCHAR(255),
    compared_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
