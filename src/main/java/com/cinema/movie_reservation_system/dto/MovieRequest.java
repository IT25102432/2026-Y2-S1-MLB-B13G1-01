package com.cinema.movie_reservation_system.dto;

import com.cinema.movie.entity.MovieStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

/**
 * Inbound payload for POST /api/v1/movies and PUT /api/v1/movies/{id}.
 * Kept separate from the entity so persistence concerns never leak into
 * the API contract (matches the pattern used in RefundService's
 * CancelBookingRequest DTO).
 */
@Data
public class MovieRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Size(max = 2000, message = "Synopsis must not exceed 2000 characters")
    private String synopsis;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be a positive number of minutes")
    private Integer durationMinutes;

    @NotBlank(message = "Language is required")
    private String language;

    @Size(max = 10)
    private String rated;

    @NotNull(message = "Release date is required")
    private LocalDate releaseDate;

    private String posterUrl;

    private String trailerUrl;

    @NotEmpty(message = "At least one genre is required")
    private Set<@NotBlank String> genres;

    private MovieStatus status;
}
