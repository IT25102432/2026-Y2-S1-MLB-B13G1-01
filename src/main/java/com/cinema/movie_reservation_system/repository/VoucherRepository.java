package com.cinema.movie_reservation_system.repository;

import com.cinema.movie_reservation_system.model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Optional<Voucher> findByVoucherCode(String voucherCode);
    List<Voucher> findByVoucherStatus(String voucherStatus);
}