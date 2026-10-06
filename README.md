# 🎬 CineBook — Web-Based Cinema Ticket Reservation System
**Repository:** `IT25102432/2026-Y2-S1-MLB-B13G1-01`  
**Course:** SE2030 — Software Engineering Project

---

## 📌 Project Architecture & Component Overview

CineBook is a full-stack, enterprise-grade Cinema Ticket Reservation System constructed with a clean, decoupled architecture:

```
2026-Y2-S1-MLB-B13G1-01/
├── backend/
│   ├── mvnw / mvnw.cmd
│   ├── pom.xml
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/cinema/movie_reservation_system/
│   │   │   │   ├── MovieReservationSystemApplication.java
│   │   │   │   ├── controller/
│   │   │   │   │   ├── HallController.java
│   │   │   │   │   ├── MovieController.java
│   │   │   │   │   ├── ShowtimeController.java
│   │   │   │   │   ├── BookingController.java
│   │   │   │   │   ├── RefundController.java
│   │   │   │   │   ├── VoucherController.java
│   │   │   │   │   └── DashboardController.java
│   │   │   │   ├── model/
│   │   │   │   │   ├── Hall.java
│   │   │   │   │   ├── Seat.java
│   │   │   │   │   ├── Movie.java
│   │   │   │   │   ├── Showtime.java
│   │   │   │   │   ├── Booking.java
│   │   │   │   │   ├── BookingSeat.java
│   │   │   │   │   ├── Refund.java
│   │   │   │   │   └── Voucher.java
│   │   │   │   ├── repository/
│   │   │   │   │   ├── HallRepository.java
│   │   │   │   │   ├── SeatRepository.java
│   │   │   │   │   ├── MovieRepository.java
│   │   │   │   │   ├── ShowtimeRepository.java
│   │   │   │   │   ├── BookingRepository.java
│   │   │   │   │   ├── RefundRepository.java
│   │   │   │   │   └── VoucherRepository.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── HallService.java
│   │   │   │   │   ├── MovieService.java
│   │   │   │   │   ├── ShowtimeService.java
│   │   │   │   │   ├── BookingService.java
│   │   │   │   │   ├── RefundService.java
│   │   │   │   │   └── VoucherService.java
│   │   │   │   └── dto/
│   │   │   │       ├── BookingRequest.java
│   │   │   │       └── RefundRequestDTO.java
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       ├── schema.sql
│   │   │       └── static/ (100% synchronized with frontend/)
│   │   │           ├── index.html
│   │   │           ├── style.css
│   │   │           └── app.js
│   │   └── test/
│   │       └── java/com/cinema/movie_reservation_system/
│   │           ├── controller/
│   │           │   ├── HallControllerTest.java
│   │           │   └── DashboardControllerTest.java
│   │           └── service/
│   │               ├── HallServiceTest.java
│   │               ├── MovieServiceTest.java
│   │               ├── ShowtimeServiceTest.java
│   │               ├── BookingServiceTest.java
│   │               ├── RefundServiceTest.java
│   │               └── VoucherServiceTest.java
├── frontend/
│   ├── index.html
│   ├── style.css
│   └── app.js
├── README.md
└── .gitignore
```

---

## 👥 Functional Module Breakdown & Group Assignments

| Module # | Student ID | Module Responsibility | Key Functional Capabilities |
|---|---|---|---|
| **1** | **IT25102154** | **Seating Layout & Hall Allocation** *(My Module)* | Hall CRUD, dynamic seat matrix (Rows A–Z, seats 1–30), VIP (Rs. 2,000) vs Standard (Rs. 1,200) pricing in Sri Lankan Rupees (LKR / Rs.), Admin maintenance toggle (`is_active`). |
| **2** | **IT25101311** | **Movie Catalog & Details Management** | Movie CRUD (title, genre, duration_mins, rating, poster_url, description), active vs archived movie filtering. |
| **3** | **IT25103071** | **Showtime Scheduling & Theater Assignment** | Showtime CRUD linking Movies, Halls, screen dates, and start times. Overlap protection logic (prevents double-booking halls within screening + 15-min turnaround buffer). |
| **4** | **IT25100266** | **Seat Reservations & Ticket Booking** | User seat selection per showtime session, seat locking, maintenance lock, 10-seat limit per transaction, booking generation (`booking_id`, `customer_name`, `total_price`), and discount voucher deduction at checkout. |
| **5** | **IT25102432** | **Refunds & Cancellations Management** | Ticket cancellation requests and refund status tracking (Pending, Approved, Rejected). **Seat Release Logic** automatically frees booked seats when a cancellation is approved. |
| **6** | **IT24100907** | **Loyalty Program & Discount Vouchers** | Promotional voucher code generation, eligibility checks, active status enforcement, and discount deduction in Sri Lankan Rupees (LKR / Rs.). |

---

## 🗄️ Database Schema (`schema.sql`)

All 8 coexisting relational tables are created and seeded with demonstration data on startup:

1. `halls` (`id`, `name`, `total_rows`, `seats_per_row`, `hall_type`, `base_price`)
2. `seats` (`id`, `hall_id`, `seat_row`, `seat_number`, `seat_type`, `is_active`, `FOREIGN KEY hall_id -> halls CASCADE`)
3. `movies` (`id`, `title`, `genre`, `duration_mins`, `rating`, `poster_url`, `description`, `status`)
4. `showtimes` (`id`, `movie_id`, `hall_id`, `show_date`, `start_time`, `FOREIGN KEY movie_id, hall_id`)
5. `bookings` (`id`, `showtime_id`, `customer_name`, `customer_email`, `total_amount_lkr`, `status`, `created_at`)
6. `booking_seats` (`id`, `booking_id`, `seat_id`, `FOREIGN KEY booking_id, seat_id`)
7. `refunds` (`id`, `booking_id`, `refund_amount_lkr`, `reason`, `status`, `requested_at`)
8. `vouchers` (`id`, `code`, `discount_amount_lkr`, `is_active`)

---

## 🛡️ Validations, Restrictions & Business Logic

### Backend Server-Side Constraints:
- **Hall Validation:** Name non-blank, `total_rows` (1–26), `seats_per_row` (1–30), `base_price` > 0 LKR.
- **Seat Status Restrictions:** Returns HTTP 400/404 if seat ID is invalid; allows admin toggle of `is_active`.
- **Showtime Overlap Collision Protection:** Rejects overlapping showtimes in the same hall within duration + 15-minute cleaning turnaround buffer with HTTP 409 / 400.
- **Booking Rules:** Enforces maximum limit of 10 seats per booking transaction. Rejects seats under maintenance (`is_active = false`) and already-booked seats.
- **Seat Release Logic:** When an admin approves a refund request (`status: APPROVED`), the associated booking status updates to `CANCELLED`, freeing all reserved seats for rebooking.
- **Voucher Validation:** Validates existence and `is_active = true` status prior to discount deduction.
- **Currency Standard:** All calculations strictly formatted in Sri Lankan Rupees (LKR / Rs.).

### Frontend Client-Side Features:
- **Atmospheric Cinema UI:** Glassmorphism dark-mode theme, glowing curved screen SVG divider, hover tooltips displaying seat coordinates and prices (e.g. `Row B - Seat 4 | VIP | Rs. 2,000`).
- **Dynamic Counters:** Live stats displaying Total Capacity, Available Seats, Under Maintenance Seats, VIP Count, and Standard Count.
- **Interactive Visual Feedback:** Animated seat selection pulses, maintenance lock, and floating toast notifications.

---

## 🚀 How to Run the Application

### Prerequisites:
- Java JDK 17 or higher
- Maven 3.8+ (or use the included Maven wrapper `.\mvnw.cmd`)

### 1. Build and Run Backend
```bash
cd backend
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

The server starts on `http://localhost:8080`.

### 2. Access the Application
- **Web UI:** [http://localhost:8080](http://localhost:8080)
- **H2 Web Console:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - JDBC URL: `jdbc:h2:mem:cinebook_db`
  - Username: `sa`
  - Password: *(empty)*

---

## 🧪 Automated Test Suite

Run the full automated test suite containing 36 unit and integration test assertions across all 6 modules:

```bash
cd backend
.\mvnw.cmd test
```

**Results:** `Tests run: 36, Failures: 0, Errors: 0, Skipped: 0` (100% Pass).
