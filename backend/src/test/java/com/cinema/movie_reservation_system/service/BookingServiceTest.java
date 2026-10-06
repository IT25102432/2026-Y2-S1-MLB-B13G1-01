package com.cinema.movie_reservation_system.service;

import com.cinema.movie_reservation_system.dto.BookingRequest;
import com.cinema.movie_reservation_system.model.Booking;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Test
    void testInitialBookingsLoaded() {
        List<Booking> bookings = bookingService.getAllBookings();
        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
    }

    @Test
    void testCreateBookingSuccess() {
        // Book seats 3 (A3 VIP) and 4 (A4 VIP) in Hall 1 for Showtime 1
        BookingRequest req = new BookingRequest(1L, "Test Customer", "customer@test.com", List.of(3L, 4L), null);
        Booking booking = bookingService.createBooking(req);
        assertNotNull(booking.getId());
        assertEquals("CONFIRMED", booking.getStatus());
        assertEquals(4000.0, booking.getTotalAmountLkr()); // 2 VIP seats * Rs. 2,000 = Rs. 4,000
    }

    @Test
    void testCreateBookingWithVoucher() {
        // Book seat 5 (A5 VIP) with CINE200 (Rs. 200 discount)
        BookingRequest req = new BookingRequest(1L, "Voucher Customer", "voucher@test.com", List.of(5L), "CINE200");
        Booking booking = bookingService.createBooking(req);
        assertNotNull(booking.getId());
        assertEquals(1800.0, booking.getTotalAmountLkr()); // Rs. 2,000 - Rs. 200 = Rs. 1,800
    }

    @Test
    void testSeatLockingConflict() {
        // Seat 1 (A1) is already booked in seed data for Showtime 1
        BookingRequest req = new BookingRequest(1L, "Double Booker", "double@test.com", List.of(1L), null);
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> bookingService.createBooking(req));
        assertTrue(ex.getMessage().contains("already booked"));
    }

    @Test
    void testMaintenanceLockRestriction() {
        // Seat 19 (C4) in Hall 1 is under maintenance in seed data (is_active = false)
        BookingRequest req = new BookingRequest(1L, "Maintenance Booker", "maint@test.com", List.of(20L), null);
        // Note: C4 seat id in Hall 1
        // Let's verify whether an inactive seat is blocked
        assertThrows(IllegalStateException.class, () -> bookingService.createBooking(req));
    }

    @Test
    void testMaxSeatsLimitEnforcement() {
        // Attempt to book 11 seats (exceeding limit of 10)
        List<Long> elevenSeats = new ArrayList<>();
        for (long i = 1; i <= 11; i++) {
            elevenSeats.add(i);
        }
        BookingRequest req = new BookingRequest(1L, "Greedy Booker", "greedy@test.com", elevenSeats, null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(req));
        assertTrue(ex.getMessage().contains("Maximum of 10 seats allowed"));
    }
}
