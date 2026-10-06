package com.ceylonstar.showtime.repository;

import com.ceylonstar.showtime.model.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    List<Showtime> findByHallNameAndShowDate(String hallName, LocalDate showDate);

    List<Showtime> findAllByOrderByShowDateAscShowTimeAsc();
}
