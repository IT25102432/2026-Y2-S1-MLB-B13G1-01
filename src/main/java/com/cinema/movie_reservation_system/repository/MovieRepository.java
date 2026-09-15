package com.cinema.movie_reservation_system.repository;

import com.cinema.movie.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * JpaSpecificationExecutor lets MovieService build dynamic WHERE clauses
 * (title/genre/status/language) without hand-writing a combinatorial
 * explosion of @Query methods.
 */
public interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {

    boolean existsByTitleIgnoreCaseAndReleaseDate(String title, java.time.LocalDate releaseDate);
}
