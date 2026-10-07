package com.movie.reservation.repository;

import com.movie.reservation.model.CinemaHall;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CinemaHallRepository extends JpaRepository<CinemaHall, Long> {

    boolean existsByHallNameIgnoreCase(String hallName);
}