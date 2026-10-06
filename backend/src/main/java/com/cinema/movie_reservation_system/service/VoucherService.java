package com.cinema.movie_reservation_system.service;

import com.cinema.movie_reservation_system.model.Voucher;
import com.cinema.movie_reservation_system.repository.VoucherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Service handling business logic for Loyalty Program & Discount Vouchers.
 * Part of Loyalty Program & Discount Vouchers (IT24100907).
 */
@Service
public class VoucherService {

    private final VoucherRepository voucherRepository;

    public VoucherService(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAll();
    }

    public Voucher getVoucherById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid voucher ID: " + id);
        }
        return voucherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Voucher not found with ID: " + id));
    }

    @Transactional
    public Voucher createVoucher(Voucher voucher) {
        if (voucher == null) {
            throw new IllegalArgumentException("Voucher data cannot be null.");
        }
        if (voucher.getCode() == null || voucher.getCode().trim().isEmpty()) {
            // Auto-generate code if not provided
            voucher.setCode("CINE" + (1000 + new Random().nextInt(9000)));
        } else {
            voucher.setCode(voucher.getCode().trim().toUpperCase());
        }

        if (voucher.getDiscountAmountLkr() == null || voucher.getDiscountAmountLkr() <= 0) {
            throw new IllegalArgumentException("Discount amount must be greater than 0 LKR.");
        }

        // Check if code is already taken
        Optional<Voucher> existing = voucherRepository.findByCode(voucher.getCode());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("A voucher with code '" + voucher.getCode() + "' already exists.");
        }

        return voucherRepository.save(voucher);
    }

    /**
     * Validates voucher code: checks if it exists and is active.
     */
    public Voucher validateVoucherCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Voucher code cannot be empty.");
        }
        String cleanCode = code.trim().toUpperCase();
        Voucher voucher = voucherRepository.findByCode(cleanCode)
                .orElseThrow(() -> new IllegalArgumentException("Voucher code '" + cleanCode + "' does not exist."));

        if (!voucher.isActive()) {
            throw new IllegalStateException("Voucher '" + cleanCode + "' is inactive or has expired.");
        }

        return voucher;
    }

    @Transactional
    public Voucher toggleVoucherStatus(Long id) {
        Voucher voucher = getVoucherById(id);
        boolean newStatus = !voucher.isActive();
        voucherRepository.updateStatus(id, newStatus);
        voucher.setActive(newStatus);
        return voucher;
    }

    @Transactional
    public void deleteVoucher(Long id) {
        getVoucherById(id);
        voucherRepository.deleteById(id);
    }
}
