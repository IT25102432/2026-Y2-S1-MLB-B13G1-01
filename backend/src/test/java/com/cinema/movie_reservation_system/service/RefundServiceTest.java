package com.cinema.movie_reservation_system.service;

import com.cinema.movie_reservation_system.dto.BookingRequest;
import com.cinema.movie_reservation_system.dto.RefundRequestDTO;
import com.cinema.movie_reservation_system.model.Booking;
import com.cinema.movie_reservation_system.model.Refund;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RefundServiceTest {

    @Autowired
    private RefundService refundService;

    @Autowired
    private BookingService bookingService;

    @Test
    void testInitialRefundsLoaded() {
        List<Refund> refunds = refundService.getAllRefunds();
        assertNotNull(refunds);
        assertFalse(refunds.isEmpty());
    }

    @Test
    void testRequestRefundAndSeatReleaseOnApproval() {
        // 1. Create a booking on seat 6 (A6)
        BookingRequest bReq = new BookingRequest(1L, "Refund User", "refund@user.com", List.of(6L), null);
        Booking booking = bookingService.createBooking(bReq);
        assertNotNull(booking.getId());
        assertEquals("CONFIRMED", booking.getStatus());

        // 2. Request refund
        RefundRequestDTO rReq = new RefundRequestDTO(booking.getId(), "Cannot make it", 2000.0);
        Refund refund = refundService.requestRefund(rReq);
        assertNotNull(refund.getId());
        assertEquals("PENDING", refund.getStatus());

        // 3. Approve refund -> Executes SEAT RELEASE LOGIC
        Refund approved = refundService.updateRefundStatus(refund.getId(), "APPROVED");
        assertEquals("APPROVED", approved.getStatus());

        // Verify booking status is now CANCELLED
        Booking refreshedBooking = bookingService.getBookingById(booking.getId());
        assertEquals("CANCELLED", refreshedBooking.getStatus());

        // 4. Now that seat 6 is released, another customer CAN book seat 6 again!
        BookingRequest newReq = new BookingRequest(1L, "New Customer", "new@user.com", List.of(6L), null);
        Booking newBooking = bookingService.createBooking(newReq);
        assertNotNull(newBooking.getId());
        assertEquals("CONFIRMED", newBooking.getStatus());
    }
}
