package com.cinema.movie_reservation_system.controller;

import com.cinema.movie_reservation_system.dto.CancelBookingRequest;
import com.cinema.movie_reservation_system.model.RefundRequest;
import com.cinema.movie_reservation_system.service.RefundService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/refunds")
public class RefundController {

    private final RefundService refundService;

    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> requestCancellation(@RequestBody CancelBookingRequest request) {
        try {
            RefundRequest savedRefund = refundService.processCancellation(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRefund);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "Cannot process cancellation. The specified bookingId or customerId does not exist in the database.",
                    "details", e.getMostSpecificCause().getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<List<RefundRequest>> getAllRefunds() {
        return ResponseEntity.ok(refundService.getAllRefunds());
    }
}