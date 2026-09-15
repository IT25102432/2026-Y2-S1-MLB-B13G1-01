package com.cinema.movie_reservation_system.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Core catalog entity representing a movie that can be booked/reserved.
 * Showtimes/Theatres are intentionally NOT modelled here - that belongs to
 * the scheduling/booking module owned by another team member. This entity
 * only owns catalog data (what a movie IS), not WHEN/WHERE it is shown.
 */
@Entity
@Table(name = "movies", indexes = {
        @Index(name = "idx_movie_title", columnList = "title"),
        @Index(name = "idx_movie_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String synopsis;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(length = 50)
    private String language;

    @Column(length = 10)
    private String rated; // e.g. PG, PG-13, R

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "poster_url", length = 500)
    private String posterUrl;

    @Column(name = "trailer_url", length = 500)
    private String trailerUrl;

    @Column(name = "average_rating")
    @Builder.Default
    private Double averageRating = 0.0;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "movie_genres", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "genre", length = 50)
    @Builder.Default
    private Set<String> genres = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private MovieStatus status = MovieStatus.COMING_SOON;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
