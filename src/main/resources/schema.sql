-- Create Database (Run separately in SSMS if not already created)
-- CREATE DATABASE MovieReservationDB;
-- GO
-- USE MovieReservationDB;
-- GO

-- Drop foreign keys and tables cleanly if resetting
IF OBJECT_ID('dbo.refund', 'U') IS NOT NULL DROP TABLE dbo.refund;
IF OBJECT_ID('dbo.ticket', 'U') IS NOT NULL DROP TABLE dbo.ticket;
IF OBJECT_ID('dbo.payment', 'U') IS NOT NULL DROP TABLE dbo.payment;
IF OBJECT_ID('dbo.voucher_application', 'U') IS NOT NULL DROP TABLE dbo.voucher_application;
IF OBJECT_ID('dbo.booking', 'U') IS NOT NULL DROP TABLE dbo.booking;
IF OBJECT_ID('dbo.show_time', 'U') IS NOT NULL DROP TABLE dbo.show_time;
IF OBJECT_ID('dbo.seat', 'U') IS NOT NULL DROP TABLE dbo.seat;
IF OBJECT_ID('dbo.cinema_hall', 'U') IS NOT NULL DROP TABLE dbo.cinema_hall;
IF OBJECT_ID('dbo.movie_genre', 'U') IS NOT NULL DROP TABLE dbo.movie_genre;
IF OBJECT_ID('dbo.movie', 'U') IS NOT NULL DROP TABLE dbo.movie;
IF OBJECT_ID('dbo.distributor_phone', 'U') IS NOT NULL DROP TABLE dbo.distributor_phone;
IF OBJECT_ID('dbo.distributor', 'U') IS NOT NULL DROP TABLE dbo.distributor;
IF OBJECT_ID('dbo.voucher', 'U') IS NOT NULL DROP TABLE dbo.voucher;
IF OBJECT_ID('dbo.loyalty_account', 'U') IS NOT NULL DROP TABLE dbo.loyalty_account;
IF OBJECT_ID('dbo.administrator', 'U') IS NOT NULL DROP TABLE dbo.administrator;
IF OBJECT_ID('dbo.box_office_staff', 'U') IS NOT NULL DROP TABLE dbo.box_office_staff;
IF OBJECT_ID('dbo.cinema_manager', 'U') IS NOT NULL DROP TABLE dbo.cinema_manager;
IF OBJECT_ID('dbo.staff', 'U') IS NOT NULL DROP TABLE dbo.staff;
IF OBJECT_ID('dbo.customer', 'U') IS NOT NULL DROP TABLE dbo.customer;
IF OBJECT_ID('dbo.user_phone', 'U') IS NOT NULL DROP TABLE dbo.user_phone;
IF OBJECT_ID('dbo.app_user', 'U') IS NOT NULL DROP TABLE dbo.app_user;

-- 1. Base User Table
CREATE TABLE app_user (
                          user_id INT IDENTITY(1,1) PRIMARY KEY,
                          username VARCHAR(50) NOT NULL UNIQUE,
                          password VARCHAR(255) NOT NULL,
                          email VARCHAR(100) NOT NULL UNIQUE,
                          first_name VARCHAR(50) NOT NULL,
                          last_name VARCHAR(50) NOT NULL
);

-- Multi-valued attribute for User Phone Numbers
CREATE TABLE user_phone (
                            user_id INT NOT NULL,
                            phone_number VARCHAR(20) NOT NULL,
                            PRIMARY KEY (user_id, phone_number),
                            FOREIGN KEY (user_id) REFERENCES app_user(user_id) ON DELETE CASCADE
);

-- 2. Customer Subclass
CREATE TABLE customer (
                          user_id INT PRIMARY KEY,
                          address VARCHAR(255),
                          age INT,
                          date_of_birth DATE,
                          FOREIGN KEY (user_id) REFERENCES app_user(user_id) ON DELETE CASCADE
);

-- 3. Staff Subclass & Roles
CREATE TABLE staff (
                       user_id INT PRIMARY KEY,
                       hire_date DATE NOT NULL,
                       FOREIGN KEY (user_id) REFERENCES app_user(user_id) ON DELETE CASCADE
);

CREATE TABLE cinema_manager (
                                user_id INT PRIMARY KEY,
                                FOREIGN KEY (user_id) REFERENCES staff(user_id) ON DELETE CASCADE
);

CREATE TABLE box_office_staff (
                                  user_id INT PRIMARY KEY,
                                  FOREIGN KEY (user_id) REFERENCES staff(user_id) ON DELETE CASCADE
);

CREATE TABLE administrator (
                               user_id INT PRIMARY KEY,
                               FOREIGN KEY (user_id) REFERENCES staff(user_id) ON DELETE CASCADE
);

-- 4. Loyalty Account
CREATE TABLE loyalty_account (
                                 account_number INT IDENTITY(1000,1) PRIMARY KEY,
                                 customer_id INT UNIQUE NOT NULL,
                                 created_date DATE NOT NULL,
                                 tier VARCHAR(50) DEFAULT 'BRONZE',
                                 points_balance INT DEFAULT 0,
                                 created_by_manager_id INT,
                                 FOREIGN KEY (customer_id) REFERENCES customer(user_id) ON DELETE CASCADE,
                                 FOREIGN KEY (created_by_manager_id) REFERENCES cinema_manager(user_id)
);

-- 5. Voucher
CREATE TABLE voucher (
                         voucher_id INT IDENTITY(1,1) PRIMARY KEY,
                         voucher_code VARCHAR(50) NOT NULL UNIQUE,
                         discount_rate DECIMAL(5,2) NOT NULL,
                         voucher_status VARCHAR(20) DEFAULT 'ACTIVE',
                         valid_from DATE NOT NULL,
                         valid_until DATE NOT NULL,
                         created_by_manager_id INT,
                         created_by_staff_id INT,
                         FOREIGN KEY (created_by_manager_id) REFERENCES cinema_manager(user_id),
                         FOREIGN KEY (created_by_staff_id) REFERENCES box_office_staff(user_id)
);

-- 6. Distributor & Multi-valued Phone
CREATE TABLE distributor (
                             distributor_id INT IDENTITY(1,1) PRIMARY KEY,
                             name VARCHAR(100) NOT NULL,
                             email VARCHAR(100) NOT NULL
);

CREATE TABLE distributor_phone (
                                   distributor_id INT NOT NULL,
                                   phone_number VARCHAR(20) NOT NULL,
                                   PRIMARY KEY (distributor_id, phone_number),
                                   FOREIGN KEY (distributor_id) REFERENCES distributor(distributor_id) ON DELETE CASCADE
);

-- 7. Movie & Multi-valued Genre
CREATE TABLE movie (
                       movie_id INT IDENTITY(1,1) PRIMARY KEY,
                       title VARCHAR(150) NOT NULL,
                       description VARCHAR(MAX),
    language VARCHAR(50),
    age_rating VARCHAR(10),
    release_date DATE,
    is_active BIT DEFAULT 1,
    distributor_id INT NOT NULL,
    managed_by_admin_id INT,
    FOREIGN KEY (distributor_id) REFERENCES distributor(distributor_id),
    FOREIGN KEY (managed_by_admin_id) REFERENCES administrator(user_id)
);

CREATE TABLE movie_genre (
                             movie_id INT NOT NULL,
                             genre VARCHAR(50) NOT NULL,
                             PRIMARY KEY (movie_id, genre),
                             FOREIGN KEY (movie_id) REFERENCES movie(movie_id) ON DELETE CASCADE
);

-- 8. Cinema Hall (Derived capacity computed column)
CREATE TABLE cinema_hall (
                             hall_no INT IDENTITY(1,1) PRIMARY KEY,
                             hall_name VARCHAR(50) NOT NULL,
                             no_of_rows INT NOT NULL,
                             no_of_columns INT NOT NULL,
                             capacity AS (no_of_rows * no_of_columns),
                             configured_by_admin_id INT,
                             FOREIGN KEY (configured_by_admin_id) REFERENCES administrator(user_id)
);

-- 9. Seat (Weak Entity)
CREATE TABLE seat (
                      row_no INT NOT NULL,
                      seat_no INT NOT NULL,
                      hall_no INT NOT NULL,
                      seat_type VARCHAR(30) DEFAULT 'STANDARD',
                      is_available BIT DEFAULT 1,
                      PRIMARY KEY (hall_no, row_no, seat_no),
                      FOREIGN KEY (hall_no) REFERENCES cinema_hall(hall_no) ON DELETE CASCADE
);

-- 10. ShowTime
CREATE TABLE show_time (
                           showtime_id INT IDENTITY(1,1) PRIMARY KEY,
                           date DATE NOT NULL,
                           start_time TIME NOT NULL,
                           end_time TIME NOT NULL,
                           movie_id INT NOT NULL,
                           hall_no INT NOT NULL,
                           scheduled_by_admin_id INT,
                           FOREIGN KEY (movie_id) REFERENCES movie(movie_id),
                           FOREIGN KEY (hall_no) REFERENCES cinema_hall(hall_no),
                           FOREIGN KEY (scheduled_by_admin_id) REFERENCES administrator(user_id)
);

-- 11. Booking
CREATE TABLE booking (
                         booking_id INT IDENTITY(1,1) PRIMARY KEY,
                         booking_date_time DATETIME NOT NULL DEFAULT GETDATE(),
                         booking_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                         total_amount DECIMAL(10,2) NOT NULL,
                         points_awarded INT DEFAULT 0,
                         customer_id INT NOT NULL,
                         loyalty_account_number INT,
                         showtime_id INT NOT NULL,
                         processed_by_staff_id INT,
                         assisted_by_staff_id INT,
                         FOREIGN KEY (customer_id) REFERENCES customer(user_id),
                         FOREIGN KEY (loyalty_account_number) REFERENCES loyalty_account(account_number),
                         FOREIGN KEY (showtime_id) REFERENCES show_time(showtime_id),
                         FOREIGN KEY (processed_by_staff_id) REFERENCES box_office_staff(user_id),
                         FOREIGN KEY (assisted_by_staff_id) REFERENCES box_office_staff(user_id)
);

CREATE TABLE voucher_application (
                                     booking_id INT NOT NULL,
                                     voucher_id INT NOT NULL,
                                     PRIMARY KEY (booking_id, voucher_id),
                                     FOREIGN KEY (booking_id) REFERENCES booking(booking_id) ON DELETE CASCADE,
                                     FOREIGN KEY (voucher_id) REFERENCES voucher(voucher_id) ON DELETE CASCADE
);

-- 12. Payment
CREATE TABLE payment (
                         payment_id INT IDENTITY(1,1) PRIMARY KEY,
                         transaction_id VARCHAR(100) NOT NULL UNIQUE,
                         amount DECIMAL(10,2) NOT NULL,
                         payment_date_time DATETIME NOT NULL DEFAULT GETDATE(),
                         payment_method VARCHAR(50) NOT NULL,
                         payment_status VARCHAR(20) NOT NULL,
                         booking_id INT UNIQUE NOT NULL,
                         FOREIGN KEY (booking_id) REFERENCES booking(booking_id) ON DELETE CASCADE
);

-- 13. Ticket
CREATE TABLE ticket (
                        ticket_id INT IDENTITY(1,1) PRIMARY KEY,
                        price DECIMAL(10,2) NOT NULL,
                        ticket_status VARCHAR(20) NOT NULL DEFAULT 'ISSUED',
                        booking_id INT NOT NULL,
                        exchanged_for_ticket_id INT UNIQUE,
                        hall_no INT NOT NULL,
                        row_no INT NOT NULL,
                        seat_no INT NOT NULL,
                        FOREIGN KEY (booking_id) REFERENCES booking(booking_id) ON DELETE CASCADE,
                        FOREIGN KEY (exchanged_for_ticket_id) REFERENCES ticket(ticket_id),
                        FOREIGN KEY (hall_no, row_no, seat_no) REFERENCES seat(hall_no, row_no, seat_no)
);

-- 14. Refund
CREATE TABLE refund (
                        refund_id INT IDENTITY(1,1) PRIMARY KEY,
                        refund_amount DECIMAL(10,2) NOT NULL,
                        refund_date DATETIME NOT NULL DEFAULT GETDATE(),
                        refund_status VARCHAR(20) NOT NULL,
                        reason VARCHAR(MAX),
    booking_id INT NOT NULL,
    processed_by_manager_id INT,
    FOREIGN KEY (booking_id) REFERENCES booking(booking_id) ON DELETE CASCADE,
    FOREIGN KEY (processed_by_manager_id) REFERENCES cinema_manager(user_id)
);