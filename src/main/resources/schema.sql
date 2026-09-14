-- 1. Movie Catalog Module (IT25101311)
CREATE TABLE IF NOT EXISTS movies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    genre VARCHAR(100),
    duration_minutes INT,
    description TEXT,
    poster_url VARCHAR(500)
);

-- 2. Cinema Hall & Seating Module (IT25102154)
-- Existing cinema_halls table kept intact for existing foreign key references
CREATE TABLE IF NOT EXISTS cinema_halls (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hall_name VARCHAR(100) NOT NULL,
    total_capacity INT NOT NULL
);

-- Halls Table for Seating Layout and Hall Allocation
CREATE TABLE IF NOT EXISTS halls (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    total_rows INT NOT NULL,
    seats_per_row INT NOT NULL,
    hall_type VARCHAR(50) NOT NULL
);

-- Seats Table for Seating Layout and Hall Allocation
CREATE TABLE IF NOT EXISTS seats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hall_id BIGINT NOT NULL,
    seat_row VARCHAR(10) NOT NULL,
    seat_number INT NOT NULL,
    seat_type VARCHAR(50) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (hall_id) REFERENCES halls(id) ON DELETE CASCADE
);

-- 3. Showtime Scheduling Module (IT25103071)
CREATE TABLE IF NOT EXISTS showtimes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    movie_id BIGINT NOT NULL,
    hall_id BIGINT NOT NULL,
    show_time DATETIME NOT NULL,
    ticket_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES movies(id),
    FOREIGN KEY (hall_id) REFERENCES cinema_halls(id)
);

-- 4. Seat Reservations Module (IT25100266)
CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    showtime_id BIGINT NOT NULL,
    booking_status VARCHAR(20) DEFAULT 'CONFIRMED',
    total_amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (showtime_id) REFERENCES showtimes(id)
);

-- 5. Refunds, Cancellations & Exchanges Module (IT25102432)
CREATE TABLE IF NOT EXISTS refunds_exchanges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT,
    customer_id BIGINT,
    original_amount DECIMAL(10,2),
    cancellation_fee DECIMAL(10,2),
    refund_amount DECIMAL(10,2),
    request_type VARCHAR(255),
    status VARCHAR(255),
    reason VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. Customer Loyalty & Vouchers Module (IT24100907)
CREATE TABLE IF NOT EXISTS loyalty_accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT UNIQUE NOT NULL,
    points_balance INT DEFAULT 0,
    voucher_code VARCHAR(50),
    discount_amount DECIMAL(10,2) DEFAULT 0.00
);

-- ==========================================================
-- Initial Seed Data: Seating Layout and Hall Allocation (IT25102154)
-- ==========================================================

-- Insert Cinema Hall "Hall 1 - IMAX" (Rows A-E, 8 seats per row = 40 seats)
INSERT INTO halls (id, name, total_rows, seats_per_row, hall_type)
VALUES (1, 'Hall 1 - IMAX', 5, 8, 'IMAX');

INSERT INTO cinema_halls (id, hall_name, total_capacity)
VALUES (1, 'Hall 1 - IMAX', 40);

-- Insert Seats for Hall 1:
-- Rows A & B: VIP seats (A1-A8, B1-B8)
-- Rows C, D & E: STANDARD seats (C1-C8, D1-D8, E1-E8)
INSERT INTO seats (hall_id, seat_row, seat_number, seat_type, is_active) VALUES
(1, 'A', 1, 'VIP', true),
(1, 'A', 2, 'VIP', true),
(1, 'A', 3, 'VIP', true),
(1, 'A', 4, 'VIP', true),
(1, 'A', 5, 'VIP', true),
(1, 'A', 6, 'VIP', true),
(1, 'A', 7, 'VIP', true),
(1, 'A', 8, 'VIP', true),

(1, 'B', 1, 'VIP', true),
(1, 'B', 2, 'VIP', true),
(1, 'B', 3, 'VIP', true),
(1, 'B', 4, 'VIP', true),
(1, 'B', 5, 'VIP', true),
(1, 'B', 6, 'VIP', true),
(1, 'B', 7, 'VIP', true),
(1, 'B', 8, 'VIP', true),

(1, 'C', 1, 'STANDARD', true),
(1, 'C', 2, 'STANDARD', true),
(1, 'C', 3, 'STANDARD', true),
(1, 'C', 4, 'STANDARD', true),
(1, 'C', 5, 'STANDARD', true),
(1, 'C', 6, 'STANDARD', true),
(1, 'C', 7, 'STANDARD', true),
(1, 'C', 8, 'STANDARD', true),

(1, 'D', 1, 'STANDARD', true),
(1, 'D', 2, 'STANDARD', true),
(1, 'D', 3, 'STANDARD', true),
(1, 'D', 4, 'STANDARD', true),
(1, 'D', 5, 'STANDARD', true),
(1, 'D', 6, 'STANDARD', true),
(1, 'D', 7, 'STANDARD', true),
(1, 'D', 8, 'STANDARD', true),

(1, 'E', 1, 'STANDARD', true),
(1, 'E', 2, 'STANDARD', true),
(1, 'E', 3, 'STANDARD', true),
(1, 'E', 4, 'STANDARD', true),
(1, 'E', 5, 'STANDARD', true),
(1, 'E', 6, 'STANDARD', true),
(1, 'E', 7, 'STANDARD', true),
(1, 'E', 8, 'STANDARD', true);
