-- ==========================================================
-- Cinema Hall & Seating Layout Module (IT25102154)
-- ==========================================================

-- Halls Table for Seating Layout and Hall Allocation
CREATE TABLE IF NOT EXISTS halls (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    total_rows INT NOT NULL,
    seats_per_row INT NOT NULL,
    hall_type VARCHAR(50) NOT NULL,
    base_price DECIMAL(10,2) DEFAULT 1200.00
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

-- ==========================================================
-- Initial Seed Data: Seating Layout and Hall Allocation
-- ==========================================================

-- Insert Cinema Hall "Hall 1 - IMAX" (Rows A-E, 8 seats per row = 40 seats)
INSERT INTO halls (id, name, total_rows, seats_per_row, hall_type, base_price)
VALUES (1, 'Hall 1 - IMAX', 5, 8, 'IMAX', 1500.00);

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
