package com.cinema.movie_reservation_system.repository;

import com.cinema.movie_reservation_system.model.Showtime;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Showtime persistence using Spring JdbcTemplate.
 * Part of Showtime Scheduling & Theater Assignment (IT25103071).
 */
@Repository
public class ShowtimeRepository {

    private final JdbcTemplate jdbcTemplate;

    public ShowtimeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Showtime> showtimeRowMapper = (rs, rowNum) -> {
        Showtime s = new Showtime();
        s.setId(rs.getLong("id"));
        s.setMovieId(rs.getLong("movie_id"));
        s.setHallId(rs.getLong("hall_id"));
        Date d = rs.getDate("show_date");
        if (d != null) {
            s.setShowDate(d.toLocalDate());
        }
        Time t = rs.getTime("start_time");
        if (t != null) {
            s.setStartTime(t.toLocalTime());
        }
        try {
            s.setMovieTitle(rs.getString("movie_title"));
            s.setHallName(rs.getString("hall_name"));
            s.setDurationMins(rs.getInt("duration_mins"));
            s.setBasePrice(rs.getDouble("base_price"));
        } catch (Exception ignored) {
        }
        return s;
    };

    public List<Showtime> findAll() {
        String sql = "SELECT s.id, s.movie_id, s.hall_id, s.show_date, s.start_time, " +
                "m.title AS movie_title, m.duration_mins AS duration_mins, " +
                "h.name AS hall_name, h.base_price AS base_price " +
                "FROM showtimes s " +
                "JOIN movies m ON s.movie_id = m.id " +
                "JOIN halls h ON s.hall_id = h.id " +
                "ORDER BY s.show_date ASC, s.start_time ASC";
        return jdbcTemplate.query(sql, showtimeRowMapper);
    }

    public Optional<Showtime> findById(Long id) {
        String sql = "SELECT s.id, s.movie_id, s.hall_id, s.show_date, s.start_time, " +
                "m.title AS movie_title, m.duration_mins AS duration_mins, " +
                "h.name AS hall_name, h.base_price AS base_price " +
                "FROM showtimes s " +
                "JOIN movies m ON s.movie_id = m.id " +
                "JOIN halls h ON s.hall_id = h.id " +
                "WHERE s.id = ?";
        List<Showtime> list = jdbcTemplate.query(sql, showtimeRowMapper, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public List<Showtime> findByHallIdAndShowDate(Long hallId, LocalDate showDate) {
        String sql = "SELECT s.id, s.movie_id, s.hall_id, s.show_date, s.start_time, " +
                "m.title AS movie_title, m.duration_mins AS duration_mins, " +
                "h.name AS hall_name, h.base_price AS base_price " +
                "FROM showtimes s " +
                "JOIN movies m ON s.movie_id = m.id " +
                "JOIN halls h ON s.hall_id = h.id " +
                "WHERE s.hall_id = ? AND s.show_date = ? " +
                "ORDER BY s.start_time ASC";
        return jdbcTemplate.query(sql, showtimeRowMapper, hallId, Date.valueOf(showDate));
    }

    public Showtime save(Showtime s) {
        if (s.getId() == null) {
            String sql = "INSERT INTO showtimes (movie_id, hall_id, show_date, start_time) VALUES (?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, s.getMovieId());
                ps.setLong(2, s.getHallId());
                ps.setDate(3, Date.valueOf(s.getShowDate()));
                ps.setTime(4, Time.valueOf(s.getStartTime()));
                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();
            if (key != null) {
                s.setId(key.longValue());
            }
            return s;
        } else {
            String sql = "UPDATE showtimes SET movie_id = ?, hall_id = ?, show_date = ?, start_time = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    s.getMovieId(),
                    s.getHallId(),
                    Date.valueOf(s.getShowDate()),
                    Time.valueOf(s.getStartTime()),
                    s.getId()
            );
            return s;
        }
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM showtimes WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
