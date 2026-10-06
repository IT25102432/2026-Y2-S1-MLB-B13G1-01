package com.cinema.movie_reservation_system.controller;

import com.cinema.movie_reservation_system.dto.RefundRequestDTO;
import com.cinema.movie_reservation_system.model.Refund;
import com.cinema.movie_reservation_system.service.RefundService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Refunds & Cancellations Management.
 * Part of Refunds & Cancellations Management (IT25102432).
 */
@RestController
@RequestMapping("/api/refunds")
@CrossOrigin(origins = "*")
public class RefundController {

    private final RefundService refundService;

    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    @GetMapping
    public ResponseEntity<List<Refund>> getAllRefunds() {
        return ResponseEntity.ok(refundService.getAllRefunds());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRefundById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(refundService.getRefundById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> requestRefund(@RequestBody RefundRequestDTO dto) {
        try {
            Refund created = refundService.requestRefund(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to submit refund request", "details", e.getMessage()));
        }
    }

    /** Alias endpoint for backward compatibility */
    @PostMapping("/cancel")
    public ResponseEntity<?> requestCancellation(@RequestBody RefundRequestDTO dto) {
        return requestRefund(dto);
    }

    /**
     * Update refund status (APPROVED or REJECTED).
     * Approving a cancellation executes seat release logic.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body != null ? body.get("status") : null;
        if (status == null || status.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Status field is required (APPROVED or REJECTED)."));
        }
        try {
            Refund updated = refundService.updateRefundStatus(id, status.trim());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update refund status", "details", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRefund(@PathVariable Long id) {
        try {
            refundService.deleteRefund(id);
            return ResponseEntity.ok(Map.of("message", "Refund record #" + id + " was deleted.", "id", id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete refund", "details", e.getMessage()));
        }
    }
}
