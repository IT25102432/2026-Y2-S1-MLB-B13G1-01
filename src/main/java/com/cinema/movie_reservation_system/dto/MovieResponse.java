package com.cinema.movie_reservation_system.dto;

import com.cinema.movie.entity.MovieStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

/**
 * Outbound representation of a Movie. Never expose the JPA entity directly
 * over REST - this avoids lazy-loading issues and lets the catalog module
 * evolve its persistence model independently of the public API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponse {

    private Long id;
    private String title;
    private String synopsis;
    private Integer durationMinutes;
    private String language;
    private String rated;
    private LocalDate releaseDate;
    private String posterUrl;
    private String trailerUrl;
    private Double averageRating;
    private Set<String> genres;
    private MovieStatus status;
}
