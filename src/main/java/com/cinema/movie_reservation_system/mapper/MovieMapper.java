package com.cinema.movie_reservation_system.mapper;

import com.cinema.movie.dto.MovieRequest;
import com.cinema.movie.dto.MovieResponse;
import com.cinema.movie.entity.Movie;
import com.cinema.movie.entity.MovieStatus;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {

    public Movie toEntity(MovieRequest request) {
        return Movie.builder()
                .title(request.getTitle())
                .synopsis(request.getSynopsis())
                .durationMinutes(request.getDurationMinutes())
                .language(request.getLanguage())
                .rated(request.getRated())
                .releaseDate(request.getReleaseDate())
                .posterUrl(request.getPosterUrl())
                .trailerUrl(request.getTrailerUrl())
                .genres(request.getGenres())
                .status(request.getStatus() != null ? request.getStatus() : MovieStatus.COMING_SOON)
                .build();
    }

    /**
     * Applies an update payload onto an existing managed entity in place,
     * so JPA's dirty-checking handles the UPDATE - no need to re-fetch.
     */
    public void updateEntity(Movie movie, MovieRequest request) {
        movie.setTitle(request.getTitle());
        movie.setSynopsis(request.getSynopsis());
        movie.setDurationMinutes(request.getDurationMinutes());
        movie.setLanguage(request.getLanguage());
        movie.setRated(request.getRated());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setPosterUrl(request.getPosterUrl());
        movie.setTrailerUrl(request.getTrailerUrl());
        movie.setGenres(request.getGenres());
        if (request.getStatus() != null) {
            movie.setStatus(request.getStatus());
        }
    }

    public MovieResponse toResponse(Movie movie) {
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .synopsis(movie.getSynopsis())
                .durationMinutes(movie.getDurationMinutes())
                .language(movie.getLanguage())
                .rated(movie.getRated())
                .releaseDate(movie.getReleaseDate())
                .posterUrl(movie.getPosterUrl())
                .trailerUrl(movie.getTrailerUrl())
                .averageRating(movie.getAverageRating())
                .genres(movie.getGenres())
                .status(movie.getStatus())
                .build();
    }
}
