-- ======================================================================
-- CineBook - Web-Based Cinema Ticket Reservation System
-- Unified Database Schema & Demonstration Seed Data across all 6 Modules
-- ======================================================================

DROP TABLE IF EXISTS refunds;
DROP TABLE IF EXISTS booking_seats;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS showtimes;
DROP TABLE IF EXISTS vouchers;
DROP TABLE IF EXISTS movies;
DROP TABLE IF EXISTS seats;
DROP TABLE IF EXISTS halls;

-- ----------------------------------------------------------------------
-- 1. Halls Table (IT25102154 - Seating Layout & Hall Allocation)
-- ----------------------------------------------------------------------
CREATE TABLE halls (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    total_rows INT NOT NULL,
    seats_per_row INT NOT NULL,
    hall_type VARCHAR(50) NOT NULL,
    base_price DECIMAL(10,2) DEFAULT 1200.00
);

-- ----------------------------------------------------------------------
-- 2. Seats Table (IT25102154 - Seating Layout & Hall Allocation)
-- ----------------------------------------------------------------------
CREATE TABLE seats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hall_id BIGINT NOT NULL,
    seat_row VARCHAR(10) NOT NULL,
    seat_number INT NOT NULL,
    seat_type VARCHAR(50) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (hall_id) REFERENCES halls(id) ON DELETE CASCADE
);

-- ----------------------------------------------------------------------
-- 3. Movies Table (IT25101311 - Movie Catalog & Details Management)
-- ----------------------------------------------------------------------
CREATE TABLE movies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL UNIQUE,
    genre VARCHAR(100) NOT NULL,
    duration_mins INT NOT NULL,
    rating VARCHAR(20) NOT NULL,
    poster_url VARCHAR(500),
    description VARCHAR(1000),
    status VARCHAR(50) DEFAULT 'ACTIVE'
);

-- ----------------------------------------------------------------------
-- 4. Showtimes Table (IT25103071 - Showtime Scheduling & Theater Assignment)
-- ----------------------------------------------------------------------
CREATE TABLE showtimes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    movie_id BIGINT NOT NULL,
    hall_id BIGINT NOT NULL,
    show_date DATE NOT NULL,
    start_time TIME NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    FOREIGN KEY (hall_id) REFERENCES halls(id) ON DELETE CASCADE
);

-- ----------------------------------------------------------------------
-- 5. Bookings Table (IT25100266 - Seat Reservations & Ticket Booking)
-- ----------------------------------------------------------------------
CREATE TABLE bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    showtime_id BIGINT NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    total_amount_lkr DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) DEFAULT 'CONFIRMED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (showtime_id) REFERENCES showtimes(id) ON DELETE CASCADE
);

-- ----------------------------------------------------------------------
-- 6. Booking Seats Join Table (IT25100266 - Seat Reservations & Ticket Booking)
-- ----------------------------------------------------------------------
CREATE TABLE booking_seats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (seat_id) REFERENCES seats(id) ON DELETE CASCADE
);

-- ----------------------------------------------------------------------
-- 7. Refunds Table (IT25102432 - Refunds & Cancellations Management)
-- ----------------------------------------------------------------------
CREATE TABLE refunds (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    refund_amount_lkr DECIMAL(10,2) NOT NULL,
    reason VARCHAR(500),
    status VARCHAR(50) DEFAULT 'PENDING',
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
);

-- ----------------------------------------------------------------------
-- 8. Vouchers Table (IT24100907 - Loyalty Program & Discount Vouchers)
-- ----------------------------------------------------------------------
CREATE TABLE vouchers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    discount_amount_lkr DECIMAL(10,2) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- ======================================================================
-- Demonstration Seed Data
-- ======================================================================

-- 1. Seed Halls (IT25102154)
INSERT INTO halls (id, name, total_rows, seats_per_row, hall_type, base_price) VALUES
(1, 'Hall 1 - IMAX', 5, 8, 'IMAX', 1500.00),
(2, 'Hall 2 - Dolby VIP Lounge', 4, 6, 'VIP', 2000.00),
(3, 'Hall 3 - Standard Digital', 4, 6, 'STANDARD', 1200.00);

-- 2. Seed Seats (Hall 1: 40 seats)
-- Row A (VIP)
INSERT INTO seats (hall_id, seat_row, seat_number, seat_type, is_active) VALUES
(1, 'A', 1, 'VIP', true),
(1, 'A', 2, 'VIP', true),
(1, 'A', 3, 'VIP', true),
(1, 'A', 4, 'VIP', true),
(1, 'A', 5, 'VIP', true),
(1, 'A', 6, 'VIP', true),
(1, 'A', 7, 'VIP', true),
(1, 'A', 8, 'VIP', true),
-- Row B (VIP)
(1, 'B', 1, 'VIP', true),
(1, 'B', 2, 'VIP', true),
(1, 'B', 3, 'VIP', true),
(1, 'B', 4, 'VIP', true),
(1, 'B', 5, 'VIP', true),
(1, 'B', 6, 'VIP', true),
(1, 'B', 7, 'VIP', true),
(1, 'B', 8, 'VIP', true),
-- Row C (STANDARD - C4 under maintenance)
(1, 'C', 1, 'STANDARD', true),
(1, 'C', 2, 'STANDARD', true),
(1, 'C', 3, 'STANDARD', true),
(1, 'C', 4, 'STANDARD', false),
(1, 'C', 5, 'STANDARD', true),
(1, 'C', 6, 'STANDARD', true),
(1, 'C', 7, 'STANDARD', true),
(1, 'C', 8, 'STANDARD', true),
-- Row D (STANDARD)
(1, 'D', 1, 'STANDARD', true),
(1, 'D', 2, 'STANDARD', true),
(1, 'D', 3, 'STANDARD', true),
(1, 'D', 4, 'STANDARD', true),
(1, 'D', 5, 'STANDARD', true),
(1, 'D', 6, 'STANDARD', true),
(1, 'D', 7, 'STANDARD', true),
(1, 'D', 8, 'STANDARD', true),
-- Row E (STANDARD)
(1, 'E', 1, 'STANDARD', true),
(1, 'E', 2, 'STANDARD', true),
(1, 'E', 3, 'STANDARD', true),
(1, 'E', 4, 'STANDARD', true),
(1, 'E', 5, 'STANDARD', true),
(1, 'E', 6, 'STANDARD', true),
(1, 'E', 7, 'STANDARD', true),
(1, 'E', 8, 'STANDARD', true);

-- Seed Seats (Hall 2: 24 VIP seats)
INSERT INTO seats (hall_id, seat_row, seat_number, seat_type, is_active) VALUES
(2, 'A', 1, 'VIP', true), (2, 'A', 2, 'VIP', true), (2, 'A', 3, 'VIP', true), (2, 'A', 4, 'VIP', true), (2, 'A', 5, 'VIP', true), (2, 'A', 6, 'VIP', true),
(2, 'B', 1, 'VIP', true), (2, 'B', 2, 'VIP', true), (2, 'B', 3, 'VIP', false), (2, 'B', 4, 'VIP', true), (2, 'B', 5, 'VIP', true), (2, 'B', 6, 'VIP', true),
(2, 'C', 1, 'VIP', true), (2, 'C', 2, 'VIP', true), (2, 'C', 3, 'VIP', true), (2, 'C', 4, 'VIP', true), (2, 'C', 5, 'VIP', true), (2, 'C', 6, 'VIP', true),
(2, 'D', 1, 'VIP', true), (2, 'D', 2, 'VIP', true), (2, 'D', 3, 'VIP', true), (2, 'D', 4, 'VIP', true), (2, 'D', 5, 'VIP', true), (2, 'D', 6, 'VIP', true);

-- Seed Seats (Hall 3: 24 STANDARD seats)
INSERT INTO seats (hall_id, seat_row, seat_number, seat_type, is_active) VALUES
(3, 'A', 1, 'STANDARD', true), (3, 'A', 2, 'STANDARD', true), (3, 'A', 3, 'STANDARD', true), (3, 'A', 4, 'STANDARD', true), (3, 'A', 5, 'STANDARD', true), (3, 'A', 6, 'STANDARD', true),
(3, 'B', 1, 'STANDARD', true), (3, 'B', 2, 'STANDARD', true), (3, 'B', 3, 'STANDARD', true), (3, 'B', 4, 'STANDARD', true), (3, 'B', 5, 'STANDARD', true), (3, 'B', 6, 'STANDARD', true),
(3, 'C', 1, 'STANDARD', true), (3, 'C', 2, 'STANDARD', true), (3, 'C', 3, 'STANDARD', true), (3, 'C', 4, 'STANDARD', true), (3, 'C', 5, 'STANDARD', true), (3, 'C', 6, 'STANDARD', true),
(3, 'D', 1, 'STANDARD', true), (3, 'D', 2, 'STANDARD', true), (3, 'D', 3, 'STANDARD', true), (3, 'D', 4, 'STANDARD', true), (3, 'D', 5, 'STANDARD', true), (3, 'D', 6, 'STANDARD', true);

-- 3. Seed Movies (IT25101311)
INSERT INTO movies (id, title, genre, duration_mins, rating, poster_url, description, status) VALUES
(1, 'Avatar: The Way of Water', 'Sci-Fi / Adventure', 192, 'PG-13', 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=400&q=80', 'Jake Sully and Neytiri protect their family and their home world of Pandora from an ancient recurring threat.', 'ACTIVE'),
(2, 'Oppenheimer', 'Biography / Drama', 180, 'R', 'https://images.unsplash.com/photo-1440404653325-ab127d49abc1?auto=format&fit=crop&w=400&q=80', 'The pulse-pounding story of J. Robert Oppenheimer leading the Manhattan Project during World War II.', 'ACTIVE'),
(3, 'Dune: Part Two', 'Sci-Fi / Action', 166, 'PG-13', 'https://images.unsplash.com/photo-1478760329108-5c3ed9d495a0?auto=format&fit=crop&w=400&q=80', 'Paul Atreides unites with the Fremen to lead a fateful crusade against the Emperor and the Harkonnens.', 'ACTIVE'),
(4, 'Interstellar', 'Sci-Fi / Adventure', 169, 'PG-13', 'https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?auto=format&fit=crop&w=400&q=80', 'Exiled astronaut Cooper ventures beyond known space through a wormhole to secure mankind survival.', 'ARCHIVED');

-- 4. Seed Showtimes (IT25103071)
INSERT INTO showtimes (id, movie_id, hall_id, show_date, start_time) VALUES
(1, 1, 1, '2026-10-15', '14:00:00'),
(2, 2, 1, '2026-10-15', '18:30:00'),
(3, 3, 2, '2026-10-15', '16:00:00'),
(4, 1, 3, '2026-10-16', '11:00:00');

-- 5. Seed Bookings (IT25100266)
INSERT INTO bookings (id, showtime_id, customer_name, customer_email, total_amount_lkr, status, created_at) VALUES
(1, 1, 'Kasun Perera', 'kasun.perera@gmail.com', 4000.00, 'CONFIRMED', '2026-10-06 09:15:00'),
(2, 1, 'Nuwan Silva', 'nuwan.silva@outlook.com', 2400.00, 'CONFIRMED', '2026-10-06 09:30:00'),
(3, 2, 'Chamari Fernando', 'chamari.f@gmail.com', 4000.00, 'CONFIRMED', '2026-10-06 10:00:00');

-- 6. Seed Booking Seats (IT25100266)
-- Booking 1 (Kasun): Seats 1 (A1) and 2 (A2) in Hall 1 (VIP = Rs. 2,000 x 2 = Rs. 4,000)
INSERT INTO booking_seats (booking_id, seat_id) VALUES
(1, 1),
(1, 2);

-- Booking 2 (Nuwan): Seats 17 (C1) and 18 (C2) in Hall 1 (STANDARD = Rs. 1,200 x 2 = Rs. 2,400)
INSERT INTO booking_seats (booking_id, seat_id) VALUES
(2, 17),
(2, 18);

-- Booking 3 (Chamari): Seats 9 (B1) and 10 (B2) in Hall 1 (VIP = Rs. 2,000 x 2 = Rs. 4,000)
INSERT INTO booking_seats (booking_id, seat_id) VALUES
(3, 9),
(3, 10);

-- 7. Seed Refunds (IT25102432)
INSERT INTO refunds (id, booking_id, refund_amount_lkr, reason, status, requested_at) VALUES
(1, 2, 2400.00, 'Personal emergency conflict with show schedule', 'PENDING', '2026-10-06 10:15:00'),
(2, 3, 4000.00, 'Accidental duplicate booking reservation', 'APPROVED', '2026-10-06 10:20:00');

-- 8. Seed Vouchers (IT24100907)
INSERT INTO vouchers (id, code, discount_amount_lkr, is_active) VALUES
(1, 'CINE200', 200.00, true),
(2, 'VIP500', 500.00, true),
(3, 'WELCOME100', 100.00, true),
(4, 'STUDENT150', 150.00, true),
(5, 'EXPIRED50', 50.00, false);
