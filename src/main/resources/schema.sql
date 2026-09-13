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
CREATE TABLE IF NOT EXISTS cinema_halls (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            hall_name VARCHAR(100) NOT NULL,
    total_capacity INT NOT NULL
    );

CREATE TABLE IF NOT EXISTS seats (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     hall_id BIGINT NOT NULL,
                                     seat_number VARCHAR(10) NOT NULL,
    seat_type VARCHAR(20) DEFAULT 'STANDARD',
    FOREIGN KEY (hall_id) REFERENCES cinema_halls(id)
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

-- 5. Refunds, Cancellations & Exchanges Module (IT25102432 - You)
CREATE TABLE refunds_exchanges (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   booking_id BIGINT,
                                   customer_id BIGINT,
                                   original_amount DECIMAL(10,2),
                                   cancellation_fee DECIMAL(10,2),
                                   refund_amount DECIMAL(10,2),
                                   request_type VARCHAR(255),
                                   status VARCHAR(255)
);

-- 6. Customer Loyalty & Vouchers Module (IT24100907)
CREATE TABLE IF NOT EXISTS loyalty_accounts (
                                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                customer_id BIGINT UNIQUE NOT NULL,
                                                points_balance INT DEFAULT 0,
                                                voucher_code VARCHAR(50),
    discount_amount DECIMAL(10,2) DEFAULT 0.00
    );