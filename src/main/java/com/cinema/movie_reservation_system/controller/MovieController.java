package com.cinema.movie.controller;

import com.cinema.movie.dto.MovieRequest;
import com.cinema.movie.dto.MovieResponse;
import com.cinema.movie.entity.MovieStatus;
import com.cinema.movie.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Catalog endpoints. Booking/showtime endpoints belong to a different
 * controller owned by whoever handles scheduling - this controller only
 * answers "what movies exist / are showing", not "when/where".
 */
@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    public ResponseEntity<MovieResponse> createMovie(@Valid @RequestBody MovieRequest request) {
        MovieResponse created = movieService.createMovie(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieResponse> updateMovie(@PathVariable Long id,
                                                     @Valid @RequestBody MovieRequest request) {
        return ResponseEntity.ok(movieService.updateMovie(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getMovie(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    /**
     * Unified search/browse endpoint.
     * e.g. GET /api/v1/movies?title=batman&genre=action&page=0&size=12&sort=releaseDate,desc
     */
    @GetMapping
    public ResponseEntity<Page<MovieResponse>> searchMovies(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) MovieStatus status,
            @PageableDefault(size = 20, sort = "releaseDate") Pageable pageable) {
        return ResponseEntity.ok(movieService.searchMovies(title, genre, language, status, pageable));
    }

    @GetMapping("/now-showing")
    public ResponseEntity<Page<MovieResponse>> getNowShowing(
            @PageableDefault(size = 20, sort = "releaseDate") Pageable pageable) {
        return ResponseEntity.ok(movieService.getNowShowing(pageable));
    }

    @GetMapping("/coming-soon")
    public ResponseEntity<Page<MovieResponse>> getComingSoon(
            @PageableDefault(size = 20, sort = "releaseDate") Pageable pageable) {
        return ResponseEntity.ok(movieService.getComingSoon(pageable));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<MovieResponse> updateStatus(@PathVariable Long id,
                                                      @RequestParam MovieStatus status) {
        return ResponseEntity.ok(movieService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}
