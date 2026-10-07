package com.movie.reservation.repository;

import com.movie.reservation.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByHallIdOrderByRowLabelAscSeatNumberAsc(Long hallId);

    boolean existsByHallIdAndSeatCodeIgnoreCase(Long hallId, String seatCode);

    void deleteByHallId(Long hallId);
}