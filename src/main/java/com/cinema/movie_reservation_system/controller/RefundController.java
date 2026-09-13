package com.cinema.movie_reservation_system.controller;

import com.cinema.movie_reservation_system.model.RefundRequest;
import com.cinema.movie_reservation_system.repository.RefundRepository;
import com.cinema.movie_reservation_system.service.RefundService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/refunds")
public class RefundController {

    private final RefundService refundService;
    private final RefundRepository refundRepository;

    public RefundController(RefundService refundService, RefundRepository refundRepository) {
        this.refundService = refundService;
        this.refundRepository = refundRepository;
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> requestCancellation(@RequestBody Map<String, Object> payload) {
        try {
            RefundRequest refund = new RefundRequest();

            if (payload.get("bookingId") != null) {
                refund.setBookingId(Long.parseLong(payload.get("bookingId").toString()));
            }
            if (payload.get("customerId") != null) {
                refund.setCustomerId(Long.parseLong(payload.get("customerId").toString()));
            }
            if (payload.get("reason") != null) {
                refund.setReason(payload.get("reason").toString());
            }

            BigDecimal originalAmount = payload.get("originalAmount") != null
                    ? new BigDecimal(payload.get("originalAmount").toString())
                    : BigDecimal.ZERO;

            BigDecimal cancellationFee = originalAmount.multiply(new BigDecimal("0.10")); // 10% fee
            BigDecimal refundAmount = originalAmount.subtract(cancellationFee);

            refund.setOriginalAmount(originalAmount);
            refund.setCancellationFee(cancellationFee);
            refund.setRefundAmount(refundAmount);
            refund.setRequestType("CANCELLATION");
            refund.setStatus("PROCESSED");

            RefundRequest savedRefund = refundRepository.save(refund);
            return ResponseEntity.ok(savedRefund);

        } catch (Exception e) {
            // Handles Foreign Key Constraint Violation cleanly
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "Cannot process cancellation. The specified bookingId or customerId does not exist in the database.",
                    "details", e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<List<RefundRequest>> getAllRefunds() {
        return ResponseEntity.ok(refundRepository.findAll());
    }
}