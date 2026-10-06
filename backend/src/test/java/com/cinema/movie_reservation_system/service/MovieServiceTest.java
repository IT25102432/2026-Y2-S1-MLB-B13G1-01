package com.cinema.movie_reservation_system.service;

import com.cinema.movie_reservation_system.model.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MovieServiceTest {

    @Autowired
    private MovieService movieService;

    @Test
    void testInitialMoviesSeeded() {
        List<Movie> movies = movieService.getAllMovies(null);
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
        assertTrue(movies.stream().anyMatch(m -> m.getTitle().contains("Avatar")));
    }

    @Test
    void testFilterByStatus() {
        List<Movie> active = movieService.getAllMovies("ACTIVE");
        assertNotNull(active);
        assertTrue(active.stream().allMatch(m -> "ACTIVE".equalsIgnoreCase(m.getStatus())));

        List<Movie> archived = movieService.getAllMovies("ARCHIVED");
        assertNotNull(archived);
        assertTrue(archived.stream().allMatch(m -> "ARCHIVED".equalsIgnoreCase(m.getStatus())));
    }

    @Test
    void testCreateMovieValidation() {
        Movie movie = new Movie();
        assertThrows(IllegalArgumentException.class, () -> movieService.createMovie(movie));

        movie.setTitle("The Matrix");
        movie.setDurationMins(136);
        movie.setGenre("Sci-Fi");
        movie.setRating("R");
        Movie created = movieService.createMovie(movie);
        assertNotNull(created.getId());
        assertEquals("The Matrix", created.getTitle());
    }

    @Test
    void testUpdateMovie() {
        Movie movie = new Movie("Movie to Update", "Action", 120, "PG-13", null, "Desc", "ACTIVE");
        Movie created = movieService.createMovie(movie);

        Movie update = new Movie();
        update.setTitle("Updated Title");
        update.setStatus("ARCHIVED");
        Movie updated = movieService.updateMovie(created.getId(), update);
        assertEquals("Updated Title", updated.getTitle());
        assertEquals("ARCHIVED", updated.getStatus());
    }

    @Test
    void testDuplicateMovieTitleThrows() {
        Movie m1 = new Movie("Inception 2", "Sci-Fi", 150, "PG-13", null, "Desc", "ACTIVE");
        movieService.createMovie(m1);

        Movie m2 = new Movie("inception 2", "Sci-Fi", 140, "PG-13", null, "Desc", "ACTIVE");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> movieService.createMovie(m2));
        assertTrue(ex.getMessage().contains("Movie title already exists"));
    }

    @Test
    void testInvalidDurationThrows() {
        Movie m = new Movie("Zero Duration Movie", "Action", 0, "PG-13", null, "Desc", "ACTIVE");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> movieService.createMovie(m));
        assertTrue(ex.getMessage().contains("duration must be greater than zero"));
    }
}
