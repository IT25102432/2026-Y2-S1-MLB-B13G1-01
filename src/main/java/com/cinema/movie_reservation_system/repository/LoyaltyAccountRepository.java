package com.cinema.movie_reservation_system.repository;

import com.cinema.movie_reservation_system.model.LoyaltyAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LoyaltyAccountRepository extends JpaRepository<LoyaltyAccount, Long> {
    Optional<LoyaltyAccount> findByCustomerUserId(Long customerId);
    boolean existsByCustomerUserId(Long customerId);
}