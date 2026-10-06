package com.cinema.movie_reservation_system.repository;

import com.cinema.movie_reservation_system.model.Movie;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Movie Catalog management using Spring JdbcTemplate.
 * Part of Movie Catalog & Details Management (IT25101311).
 */
@Repository
public class MovieRepository {

    private final JdbcTemplate jdbcTemplate;

    public MovieRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Movie> movieRowMapper = (rs, rowNum) -> new Movie(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getString("genre"),
            rs.getInt("duration_mins"),
            rs.getString("rating"),
            rs.getString("poster_url"),
            rs.getString("description"),
            rs.getString("status")
    );

    public List<Movie> findAll() {
        String sql = "SELECT id, title, genre, duration_mins, rating, poster_url, description, status FROM movies ORDER BY id ASC";
        return jdbcTemplate.query(sql, movieRowMapper);
    }

    public List<Movie> findByStatus(String status) {
        String sql = "SELECT id, title, genre, duration_mins, rating, poster_url, description, status FROM movies WHERE UPPER(status) = UPPER(?) ORDER BY id ASC";
        return jdbcTemplate.query(sql, movieRowMapper, status);
    }

    public Optional<Movie> findById(Long id) {
        String sql = "SELECT id, title, genre, duration_mins, rating, poster_url, description, status FROM movies WHERE id = ?";
        List<Movie> movies = jdbcTemplate.query(sql, movieRowMapper, id);
        return movies.isEmpty() ? Optional.empty() : Optional.of(movies.get(0));
    }

    public Movie save(Movie movie) {
        if (movie.getId() == null) {
            String sql = "INSERT INTO movies (title, genre, duration_mins, rating, poster_url, description, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, movie.getTitle());
                ps.setString(2, movie.getGenre());
                ps.setInt(3, movie.getDurationMins());
                ps.setString(4, movie.getRating());
                ps.setString(5, movie.getPosterUrl());
                ps.setString(6, movie.getDescription());
                ps.setString(7, movie.getStatus() != null ? movie.getStatus().toUpperCase() : "ACTIVE");
                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();
            if (key != null) {
                movie.setId(key.longValue());
            }
            return movie;
        } else {
            String sql = "UPDATE movies SET title = ?, genre = ?, duration_mins = ?, rating = ?, poster_url = ?, description = ?, status = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    movie.getTitle(),
                    movie.getGenre(),
                    movie.getDurationMins(),
                    movie.getRating(),
                    movie.getPosterUrl(),
                    movie.getDescription(),
                    movie.getStatus() != null ? movie.getStatus().toUpperCase() : "ACTIVE",
                    movie.getId()
            );
            return movie;
        }
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM movies WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
