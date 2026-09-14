//change if needed
package com.cinema.movie_reservation_system.repository;

import com.cinema.movie_reservation_system.model.CinemaManager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CinemaManagerRepository extends JpaRepository<CinemaManager, Long> {
}