//change if needed
package com.cinema.movie_reservation_system.repository;

import com.cinema.movie_reservation_system.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}