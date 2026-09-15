package com.cinema.movie_reservation_system.service;

import com.cinema.movie.dto.MovieRequest;
import com.cinema.movie.dto.MovieResponse;
import com.cinema.movie.entity.MovieStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovieService {

    MovieResponse createMovie(MovieRequest request);

    MovieResponse updateMovie(Long id, MovieRequest request);

    MovieResponse getMovieById(Long id);

    Page<MovieResponse> searchMovies(String title, String genre, String language, MovieStatus status, Pageable pageable);

    Page<MovieResponse> getNowShowing(Pageable pageable);

    Page<MovieResponse> getComingSoon(Pageable pageable);

    MovieResponse updateStatus(Long id, MovieStatus status);

    void deleteMovie(Long id);
}
