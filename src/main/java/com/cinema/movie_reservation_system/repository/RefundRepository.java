package com.cinema.movie_reservation_system.repository;

import com.cinema.movie_reservation_system.model.RefundRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RefundRepository extends JpaRepository<RefundRequest, Long> {
    List<RefundRequest> findByCustomerId(Long customerId);
}