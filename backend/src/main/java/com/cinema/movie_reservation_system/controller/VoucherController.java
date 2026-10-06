package com.cinema.movie_reservation_system.controller;

import com.cinema.movie_reservation_system.model.Voucher;
import com.cinema.movie_reservation_system.service.VoucherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Loyalty Program & Discount Vouchers.
 * Part of Loyalty Program & Discount Vouchers (IT24100907).
 */
@RestController
@RequestMapping("/api/vouchers")
@CrossOrigin(origins = "*")
public class VoucherController {

    private final VoucherService voucherService;

    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping
    public ResponseEntity<List<Voucher>> getAllVouchers() {
        return ResponseEntity.ok(voucherService.getAllVouchers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVoucherById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(voucherService.getVoucherById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createVoucher(@RequestBody Voucher voucher) {
        try {
            Voucher created = voucherService.createVoucher(voucher);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create voucher", "details", e.getMessage()));
        }
    }

    @GetMapping("/validate/{code}")
    public ResponseEntity<?> validateCode(@PathVariable String code) {
        try {
            Voucher voucher = voucherService.validateVoucherCode(code);
            return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "code", voucher.getCode(),
                    "discountAmountLkr", voucher.getDiscountAmountLkr(),
                    "message", "Voucher applied! Saved Rs. " + String.format("%.2f", voucher.getDiscountAmountLkr())
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("valid", false, "error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("valid", false, "error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggleVoucherStatus(@PathVariable Long id) {
        try {
            Voucher updated = voucherService.toggleVoucherStatus(id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVoucher(@PathVariable Long id) {
        try {
            voucherService.deleteVoucher(id);
            return ResponseEntity.ok(Map.of("message", "Voucher #" + id + " was deleted.", "id", id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete voucher", "details", e.getMessage()));
        }
    }
}
