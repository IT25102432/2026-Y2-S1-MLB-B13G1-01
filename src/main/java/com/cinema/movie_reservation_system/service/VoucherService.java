package com.cinema.movie_reservation_system.service;

import com.cinema.movie_reservation_system.model.CinemaManager;
import com.cinema.movie_reservation_system.model.Voucher;
import com.cinema.movie_reservation_system.repository.CinemaManagerRepository;
import com.cinema.movie_reservation_system.repository.VoucherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final CinemaManagerRepository cinemaManagerRepository;

    public VoucherService(VoucherRepository voucherRepository,
                          CinemaManagerRepository cinemaManagerRepository) {
        this.voucherRepository = voucherRepository;
        this.cinemaManagerRepository = cinemaManagerRepository;
    }

    public List<Voucher> findAll() {
        return voucherRepository.findAll();
    }

    public Voucher getById(Long id) {
        return voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher not found: " + id));
    }

    @Transactional
    public Voucher createVoucher(BigDecimal discountRate, LocalDate validFrom, LocalDate validUntil,
                                 Long createdByManagerId, String customCode) {
        Voucher voucher = new Voucher();
        voucher.setVoucherCode(customCode != null && !customCode.isBlank()
                ? customCode
                : UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        voucher.setDiscountRate(discountRate);
        voucher.setVoucherStatus("ACTIVE");
        voucher.setValidFrom(validFrom);
        voucher.setValidUntil(validUntil);

        if (createdByManagerId != null) {
            CinemaManager manager = cinemaManagerRepository.findById(createdByManagerId)
                    .orElseThrow(() -> new RuntimeException("Cinema manager not found: " + createdByManagerId));
            voucher.setCreatedByManager(manager);
        }
        return voucherRepository.save(voucher);
    }

    /** Validates a code and returns the voucher. Throws if invalid/expired. */
    public Voucher validateCode(String code) {
        Voucher voucher = voucherRepository.findByVoucherCode(code)
                .orElseThrow(() -> new RuntimeException("Invalid voucher code."));
        if (!"ACTIVE".equals(voucher.getVoucherStatus())) {
            throw new RuntimeException("Voucher is not active.");
        }
        LocalDate today = LocalDate.now();
        if (today.isBefore(voucher.getValidFrom())) {
            throw new RuntimeException("Voucher is not yet valid.");
        }
        if (today.isAfter(voucher.getValidUntil())) {
            throw new RuntimeException("Voucher has expired.");
        }
        return voucher;
    }

    /** Marks a voucher as used after checkout. */
    @Transactional
    public Voucher markUsed(Long voucherId) {
        Voucher voucher = getById(voucherId);
        voucher.setVoucherStatus("USED");
        return voucherRepository.save(voucher);
    }
}