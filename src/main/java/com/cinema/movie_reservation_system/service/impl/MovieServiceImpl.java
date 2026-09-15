package com.cinema.movie_reservation_system.service.impl;

import com.cinema.movie.dto.MovieRequest;
import com.cinema.movie.dto.MovieResponse;
import com.cinema.movie.entity.Movie;
import com.cinema.movie.entity.MovieStatus;
import com.cinema.movie.exception.DuplicateMovieException;
import com.cinema.movie.exception.MovieNotFoundException;
import com.cinema.movie.mapper.MovieMapper;
import com.cinema.movie.repository.MovieRepository;
import com.cinema.movie.repository.MovieSpecification;
import com.cinema.movie.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    @Override
    public MovieResponse createMovie(MovieRequest request) {
        if (movieRepository.existsByTitleIgnoreCaseAndReleaseDate(request.getTitle(), request.getReleaseDate())) {
            throw new DuplicateMovieException(request.getTitle());
        }
        Movie movie = movieMapper.toEntity(request);
        Movie saved = movieRepository.save(movie);
        return movieMapper.toResponse(saved);
    }

    @Override
    public MovieResponse updateMovie(Long id, MovieRequest request) {
        Movie movie = findMovieOrThrow(id);
        movieMapper.updateEntity(movie, request);
        // save() is not strictly required inside a @Transactional method
        // (dirty checking handles it), but it's kept explicit for clarity.
        Movie updated = movieRepository.save(movie);
        return movieMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public MovieResponse getMovieById(Long id) {
        return movieMapper.toResponse(findMovieOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovieResponse> searchMovies(String title, String genre, String language, MovieStatus status, Pageable pageable) {
        return movieRepository
                .findAll(MovieSpecification.filterBy(title, genre, language, status), pageable)
                .map(movieMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovieResponse> getNowShowing(Pageable pageable) {
        return searchMovies(null, null, null, MovieStatus.NOW_SHOWING, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovieResponse> getComingSoon(Pageable pageable) {
        return searchMovies(null, null, null, MovieStatus.COMING_SOON, pageable);
    }

    @Override
    public MovieResponse updateStatus(Long id, MovieStatus status) {
        Movie movie = findMovieOrThrow(id);
        movie.setStatus(status);
        return movieMapper.toResponse(movieRepository.save(movie));
    }

    @Override
    public void deleteMovie(Long id) {
        Movie movie = findMovieOrThrow(id);
        movieRepository.delete(movie);
    }

    private Movie findMovieOrThrow(Long id) {
        return movieRepository.findById(id).orElseThrow(() -> new MovieNotFoundException(id));
    }
}
