package com.cinema.movie_reservation_system.service;

import com.cinema.movie_reservation_system.dto.BookingRequest;
import com.cinema.movie_reservation_system.model.Booking;
import com.cinema.movie_reservation_system.model.Hall;
import com.cinema.movie_reservation_system.model.Seat;
import com.cinema.movie_reservation_system.model.Showtime;
import com.cinema.movie_reservation_system.model.Voucher;
import com.cinema.movie_reservation_system.repository.BookingRepository;
import com.cinema.movie_reservation_system.repository.HallRepository;
import com.cinema.movie_reservation_system.repository.SeatRepository;
import com.cinema.movie_reservation_system.repository.ShowtimeRepository;
import com.cinema.movie_reservation_system.repository.VoucherRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service handling business logic for Seat Reservations & Ticket Booking.
 * Implements seat locking, booking limits (max 10 seats), maintenance checks, and pricing.
 * Part of Seat Reservations & Ticket Booking (IT25100266).
 */
@Service
public class BookingService {

    public static final int MAX_SEATS_PER_BOOKING = 10;
    public static final double VIP_SEAT_PRICE_LKR = 2000.00;
    public static final double STANDARD_SEAT_PRICE_LKR = 1200.00;

    private final BookingRepository bookingRepository;
    private final ShowtimeRepository showtimeRepository;
    private final HallRepository hallRepository;
    private final SeatRepository seatRepository;
    private final VoucherRepository voucherRepository;
    private final JdbcTemplate jdbcTemplate;

    public BookingService(BookingRepository bookingRepository,
                          ShowtimeRepository showtimeRepository,
                          HallRepository hallRepository,
                          SeatRepository seatRepository,
                          VoucherRepository voucherRepository,
                          JdbcTemplate jdbcTemplate) {
        this.bookingRepository = bookingRepository;
        this.showtimeRepository = showtimeRepository;
        this.hallRepository = hallRepository;
        this.seatRepository = seatRepository;
        this.voucherRepository = voucherRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid booking ID: " + id);
        }
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking reservation not found with ID: " + id));
    }

    /**
     * Create a ticket booking reservation.
     * Enforces:
     * - Max 10 seats limit per booking transaction
     * - Maintenance lock: rejects seats under maintenance
     * - Seat locking: rejects already reserved seats
     * - Currency formatting & voucher discount application in LKR (Rs.)
     */
    @Transactional
    public Booking createBooking(BookingRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Booking request data cannot be null.");
        }
        if (request.getCustomerName() == null || request.getCustomerName().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty.");
        }
        if (request.getCustomerEmail() == null || request.getCustomerEmail().trim().isEmpty() || !request.getCustomerEmail().contains("@")) {
            throw new IllegalArgumentException("Valid customer email address is required.");
        }
        if (request.getShowtimeId() == null || request.getShowtimeId() <= 0) {
            throw new IllegalArgumentException("Valid showtime session ID is required.");
        }
        if (request.getSeatIds() == null || request.getSeatIds().isEmpty()) {
            throw new IllegalArgumentException("At least one seat must be selected for reservation.");
        }
        if (request.getSeatIds().size() > MAX_SEATS_PER_BOOKING) {
            throw new IllegalArgumentException("Maximum of " + MAX_SEATS_PER_BOOKING + " seats allowed per booking transaction.");
        }

        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new IllegalArgumentException("Showtime not found with ID: " + request.getShowtimeId()));

        Hall hall = hallRepository.findById(showtime.getHallId())
                .orElseThrow(() -> new IllegalArgumentException("Hall not found with ID: " + showtime.getHallId()));

        // Query already confirmed booked seat IDs for this showtime
        String bookedSeatsSql = "SELECT bs.seat_id FROM booking_seats bs " +
                "JOIN bookings b ON bs.booking_id = b.id " +
                "WHERE b.showtime_id = ? AND UPPER(b.status) = 'CONFIRMED'";
        Set<Long> alreadyBookedSeatIds = new HashSet<>(jdbcTemplate.query(
                bookedSeatsSql, (rs, rowNum) -> rs.getLong("seat_id"), showtime.getId()
        ));

        double subtotalLkr = 0.0;
        List<String> labels = new ArrayList<>();

        for (Long seatId : request.getSeatIds()) {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new IllegalArgumentException("Seat not found with ID: " + seatId));

            if (!seat.getHallId().equals(hall.getId())) {
                throw new IllegalArgumentException("Seat " + seat.getSeatLabel() + " does not belong to " + hall.getName());
            }

            // Maintenance lock constraint
            if (!seat.isActive()) {
                throw new IllegalStateException("Seat " + seat.getSeatLabel() + " is currently under maintenance and cannot be booked.");
            }

            // Seat locking constraint
            if (alreadyBookedSeatIds.contains(seatId)) {
                throw new IllegalStateException("Seat " + seat.getSeatLabel() + " is already booked for this showtime session.");
            }

            // Price calculation: VIP = Rs. 2,000, Standard = hall base_price or Rs. 1,200
            double seatPrice = "VIP".equalsIgnoreCase(seat.getSeatType())
                    ? VIP_SEAT_PRICE_LKR
                    : (hall.getBasePrice() != null ? hall.getBasePrice() : STANDARD_SEAT_PRICE_LKR);

            subtotalLkr += seatPrice;
            labels.add(seat.getSeatLabel());
        }

        // Apply voucher discount if supplied
        double discountLkr = 0.0;
        String voucherCode = null;
        if (request.getVoucherCode() != null && !request.getVoucherCode().trim().isEmpty()) {
            voucherCode = request.getVoucherCode().trim().toUpperCase();
            Optional<Voucher> voucherOpt = voucherRepository.findByCode(voucherCode);
            if (voucherOpt.isEmpty()) {
                throw new IllegalArgumentException("Invalid voucher code: " + voucherCode);
            }
            Voucher voucher = voucherOpt.get();
            if (!voucher.isActive()) {
                throw new IllegalStateException("Voucher " + voucherCode + " is not active or has expired.");
            }
            discountLkr = (voucher.getDiscountAmountLkr() != null) ? voucher.getDiscountAmountLkr() : 0.0;
        }

        double totalAmountLkr = Math.max(0.0, subtotalLkr - discountLkr);

        Booking booking = new Booking();
        booking.setShowtimeId(showtime.getId());
        booking.setCustomerName(request.getCustomerName().trim());
        booking.setCustomerEmail(request.getCustomerEmail().trim());
        booking.setTotalAmountLkr(totalAmountLkr);
        booking.setStatus("CONFIRMED");
        booking.setCreatedAt(LocalDateTime.now());
        booking.setSeatIds(request.getSeatIds());
        booking.setSeatLabels(labels);
        booking.setVoucherCode(voucherCode);
        booking.setDiscountLkr(discountLkr);

        return bookingRepository.save(booking);
    }

    @Transactional
    public void cancelBooking(Long id) {
        Booking b = getBookingById(id);
        bookingRepository.updateStatus(id, "CANCELLED");
    }

    @Transactional
    public void deleteBooking(Long id) {
        getBookingById(id);
        bookingRepository.deleteById(id);
    }
}
