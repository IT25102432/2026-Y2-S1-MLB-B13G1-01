package com.cinema.movie_reservation_system.service;

import com.cinema.movie_reservation_system.dto.RefundRequestDTO;
import com.cinema.movie_reservation_system.model.Booking;
import com.cinema.movie_reservation_system.model.Refund;
import com.cinema.movie_reservation_system.repository.BookingRepository;
import com.cinema.movie_reservation_system.repository.RefundRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service handling business logic for Refunds & Cancellations.
 * Implements seat release logic upon cancellation approval.
 * Part of Refunds & Cancellations Management (IT25102432).
 */
@Service
public class RefundService {

    private final RefundRepository refundRepository;
    private final BookingRepository bookingRepository;

    public RefundService(RefundRepository refundRepository, BookingRepository bookingRepository) {
        this.refundRepository = refundRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<Refund> getAllRefunds() {
        return refundRepository.findAll();
    }

    public Refund getRefundById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid refund ID: " + id);
        }
        return refundRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Refund record not found with ID: " + id));
    }

    @Transactional
    public Refund requestRefund(RefundRequestDTO dto) {
        if (dto == null || dto.getBookingId() == null || dto.getBookingId() <= 0) {
            throw new IllegalArgumentException("Valid booking ID is required to request a refund.");
        }

        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + dto.getBookingId()));

        if (!"CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalStateException("Only confirmed bookings are eligible for refund requests. Current status: " + booking.getStatus());
        }

        // Check for existing pending refund
        List<Refund> existingRefunds = refundRepository.findByBookingId(booking.getId());
        for (Refund r : existingRefunds) {
            if ("PENDING".equalsIgnoreCase(r.getStatus())) {
                throw new IllegalStateException("A pending refund request already exists for Booking #" + booking.getId());
            }
        }

        double amount = (dto.getRefundAmountLkr() != null && dto.getRefundAmountLkr() > 0)
                ? dto.getRefundAmountLkr()
                : booking.getTotalAmountLkr();

        String reason = (dto.getReason() != null && !dto.getReason().trim().isEmpty())
                ? dto.getReason().trim()
                : "Customer requested cancellation";

        Refund refund = new Refund();
        refund.setBookingId(booking.getId());
        refund.setRefundAmountLkr(amount);
        refund.setReason(reason);
        refund.setStatus("PENDING");
        refund.setRequestedAt(LocalDateTime.now());

        return refundRepository.save(refund);
    }

    /**
     * Update refund status (APPROVED or REJECTED).
     * SEAT RELEASE LOGIC: When a cancellation is APPROVED, the booking status is set to CANCELLED.
     * This immediately releases the locked seats so other customers can book them!
     */
    @Transactional
    public Refund updateRefundStatus(Long id, String status) {
        if (status == null || (!status.equalsIgnoreCase("APPROVED") && !status.equalsIgnoreCase("REJECTED") && !status.equalsIgnoreCase("PENDING"))) {
            throw new IllegalArgumentException("Invalid status: " + status + ". Allowed: PENDING, APPROVED, REJECTED");
        }

        Refund refund = getRefundById(id);
        String upperStatus = status.toUpperCase();

        if ("APPROVED".equalsIgnoreCase(upperStatus)) {
            // Release seats by marking the booking as CANCELLED
            bookingRepository.updateStatus(refund.getBookingId(), "CANCELLED");
        }

        refundRepository.updateStatus(id, upperStatus);
        refund.setStatus(upperStatus);
        return refund;
    }

    @Transactional
    public void deleteRefund(Long id) {
        getRefundById(id);
        refundRepository.deleteById(id);
    }
}
