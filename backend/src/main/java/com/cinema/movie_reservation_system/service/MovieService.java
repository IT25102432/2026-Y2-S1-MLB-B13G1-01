package com.cinema.movie_reservation_system.service;

import com.cinema.movie_reservation_system.model.Movie;
import com.cinema.movie_reservation_system.repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service handling business logic for Movie Catalog & Details Management.
 * Part of Movie Catalog & Details Management (IT25101311).
 */
@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<Movie> getAllMovies(String status) {
        if (status != null && !status.trim().isEmpty()) {
            return movieRepository.findByStatus(status.trim());
        }
        return movieRepository.findAll();
    }

    public Movie getMovieById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid movie ID: " + id);
        }
        return movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with ID: " + id));
    }

    @Transactional
    public Movie createMovie(Movie movie) {
        validateMovie(movie);
        return movieRepository.save(movie);
    }

    @Transactional
    public Movie updateMovie(Long id, Movie incoming) {
        Movie existing = getMovieById(id);

        if (incoming.getTitle() != null) {
            if (incoming.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("Movie title cannot be empty.");
            }
            existing.setTitle(incoming.getTitle().trim());
        }
        if (incoming.getGenre() != null && !incoming.getGenre().trim().isEmpty()) {
            existing.setGenre(incoming.getGenre().trim());
        }
        if (incoming.getDurationMins() != null) {
            if (incoming.getDurationMins() <= 0) {
                throw new IllegalArgumentException("Duration must be greater than zero.");
            }
            existing.setDurationMins(incoming.getDurationMins());
        }
        if (incoming.getRating() != null && !incoming.getRating().trim().isEmpty()) {
            existing.setRating(incoming.getRating().trim());
        }
        if (incoming.getPosterUrl() != null) {
            existing.setPosterUrl(incoming.getPosterUrl().trim());
        }
        if (incoming.getDescription() != null) {
            existing.setDescription(incoming.getDescription().trim());
        }
        if (incoming.getStatus() != null && !incoming.getStatus().trim().isEmpty()) {
            existing.setStatus(incoming.getStatus().trim().toUpperCase());
        }

        return movieRepository.save(existing);
    }

    @Transactional
    public void deleteMovie(Long id) {
        getMovieById(id);
        movieRepository.deleteById(id);
    }

    private void validateMovie(Movie movie) {
        if (movie == null) {
            throw new IllegalArgumentException("Movie data cannot be null.");
        }
        if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Movie title cannot be empty.");
        }
        if (movie.getDurationMins() == null || movie.getDurationMins() <= 0) {
            throw new IllegalArgumentException("Movie duration must be greater than zero minutes.");
        }
        if (movie.getGenre() == null || movie.getGenre().trim().isEmpty()) {
            movie.setGenre("General");
        }
        if (movie.getRating() == null || movie.getRating().trim().isEmpty()) {
            movie.setRating("PG");
        }
        if (movie.getStatus() == null || movie.getStatus().trim().isEmpty()) {
            movie.setStatus("ACTIVE");
        }
    }
}
