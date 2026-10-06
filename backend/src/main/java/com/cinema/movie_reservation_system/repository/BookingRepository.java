package com.cinema.movie_reservation_system.repository;

import com.cinema.movie_reservation_system.model.Booking;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Booking & Reservations persistence using Spring JdbcTemplate.
 * Part of Seat Reservations & Ticket Booking (IT25100266).
 */
@Repository
public class BookingRepository {

    private final JdbcTemplate jdbcTemplate;

    public BookingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Booking> bookingRowMapper = (rs, rowNum) -> {
        Booking b = new Booking();
        b.setId(rs.getLong("id"));
        b.setShowtimeId(rs.getLong("showtime_id"));
        b.setCustomerName(rs.getString("customer_name"));
        b.setCustomerEmail(rs.getString("customer_email"));
        b.setTotalAmountLkr(rs.getDouble("total_amount_lkr"));
        b.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            b.setCreatedAt(ts.toLocalDateTime());
        }
        try {
            b.setMovieTitle(rs.getString("movie_title"));
            b.setHallName(rs.getString("hall_name"));
            Date d = rs.getDate("show_date");
            if (d != null) {
                b.setShowDate(d.toLocalDate());
            }
            Time t = rs.getTime("start_time");
            if (t != null) {
                b.setStartTime(t.toLocalTime());
            }
        } catch (Exception ignored) {
        }
        return b;
    };

    public List<Booking> findAll() {
        String sql = "SELECT b.id, b.showtime_id, b.customer_name, b.customer_email, b.total_amount_lkr, b.status, b.created_at, " +
                "m.title AS movie_title, h.name AS hall_name, s.show_date, s.start_time " +
                "FROM bookings b " +
                "JOIN showtimes s ON b.showtime_id = s.id " +
                "JOIN movies m ON s.movie_id = m.id " +
                "JOIN halls h ON s.hall_id = h.id " +
                "ORDER BY b.id DESC";
        List<Booking> bookings = jdbcTemplate.query(sql, bookingRowMapper);
        for (Booking b : bookings) {
            populateBookingSeats(b);
        }
        return bookings;
    }

    public Optional<Booking> findById(Long id) {
        String sql = "SELECT b.id, b.showtime_id, b.customer_name, b.customer_email, b.total_amount_lkr, b.status, b.created_at, " +
                "m.title AS movie_title, h.name AS hall_name, s.show_date, s.start_time " +
                "FROM bookings b " +
                "JOIN showtimes s ON b.showtime_id = s.id " +
                "JOIN movies m ON s.movie_id = m.id " +
                "JOIN halls h ON s.hall_id = h.id " +
                "WHERE b.id = ?";
        List<Booking> list = jdbcTemplate.query(sql, bookingRowMapper, id);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        Booking b = list.get(0);
        populateBookingSeats(b);
        return Optional.of(b);
    }

    public Booking save(Booking b) {
        if (b.getId() == null) {
            String sql = "INSERT INTO bookings (showtime_id, customer_name, customer_email, total_amount_lkr, status, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"ID"});
                ps.setLong(1, b.getShowtimeId());
                ps.setString(2, b.getCustomerName());
                ps.setString(3, b.getCustomerEmail());
                ps.setDouble(4, b.getTotalAmountLkr());
                ps.setString(5, b.getStatus() != null ? b.getStatus().toUpperCase() : "CONFIRMED");
                ps.setTimestamp(6, b.getCreatedAt() != null ? Timestamp.valueOf(b.getCreatedAt()) : new Timestamp(System.currentTimeMillis()));
                return ps;
            }, keyHolder);

            Number key = null;
            if (keyHolder.getKeys() != null && keyHolder.getKeys().containsKey("ID")) {
                key = (Number) keyHolder.getKeys().get("ID");
            } else if (keyHolder.getKeys() != null && keyHolder.getKeys().containsKey("id")) {
                key = (Number) keyHolder.getKeys().get("id");
            } else if (keyHolder.getKey() != null) {
                key = keyHolder.getKey();
            }
            if (key != null) {
                b.setId(key.longValue());
            }

            // Save seat associations
            if (b.getSeatIds() != null && !b.getSeatIds().isEmpty()) {
                saveBookingSeats(b.getId(), b.getSeatIds());
            }

            return b;
        } else {
            String sql = "UPDATE bookings SET showtime_id = ?, customer_name = ?, customer_email = ?, total_amount_lkr = ?, status = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    b.getShowtimeId(),
                    b.getCustomerName(),
                    b.getCustomerEmail(),
                    b.getTotalAmountLkr(),
                    b.getStatus() != null ? b.getStatus().toUpperCase() : "CONFIRMED",
                    b.getId()
            );
            return b;
        }
    }

    public int updateStatus(Long id, String status) {
        String sql = "UPDATE bookings SET status = ? WHERE id = ?";
        return jdbcTemplate.update(sql, status.toUpperCase(), id);
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM bookings WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public void saveBookingSeats(Long bookingId, List<Long> seatIds) {
        String sql = "INSERT INTO booking_seats (booking_id, seat_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, bookingId);
                ps.setLong(2, seatIds.get(i));
            }

            @Override
            public int getBatchSize() {
                return seatIds.size();
            }
        });
    }

    private void populateBookingSeats(Booking b) {
        String seatSql = "SELECT s.id, s.seat_row, s.seat_number FROM booking_seats bs " +
                "JOIN seats s ON bs.seat_id = s.id " +
                "WHERE bs.booking_id = ? ORDER BY s.seat_row ASC, s.seat_number ASC";
        List<Long> seatIds = jdbcTemplate.query(seatSql, (rs, rowNum) -> rs.getLong("id"), b.getId());
        List<String> seatLabels = jdbcTemplate.query(seatSql, (rs, rowNum) -> rs.getString("seat_row") + rs.getInt("seat_number"), b.getId());
        b.setSeatIds(seatIds);
        b.setSeatLabels(seatLabels);
    }
}
