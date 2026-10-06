package com.cinema.movie_reservation_system.repository;

import com.cinema.movie_reservation_system.model.Refund;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Refund persistence using Spring JdbcTemplate.
 * Part of Refunds & Cancellations Management (IT25102432).
 */
@Repository
public class RefundRepository {

    private final JdbcTemplate jdbcTemplate;

    public RefundRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Refund> refundRowMapper = (rs, rowNum) -> {
        Refund r = new Refund();
        r.setId(rs.getLong("id"));
        r.setBookingId(rs.getLong("booking_id"));
        r.setRefundAmountLkr(rs.getDouble("refund_amount_lkr"));
        r.setReason(rs.getString("reason"));
        r.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("requested_at");
        if (ts != null) {
            r.setRequestedAt(ts.toLocalDateTime());
        }
        try {
            r.setCustomerName(rs.getString("customer_name"));
            r.setCustomerEmail(rs.getString("customer_email"));
            r.setBookingTotalLkr(rs.getDouble("total_amount_lkr"));
            r.setMovieTitle(rs.getString("movie_title"));
            r.setHallName(rs.getString("hall_name"));
            Date d = rs.getDate("show_date");
            if (d != null) {
                r.setShowDate(d.toLocalDate());
            }
            Time t = rs.getTime("start_time");
            if (t != null) {
                r.setStartTime(t.toLocalTime());
            }
        } catch (Exception ignored) {
        }
        return r;
    };

    public List<Refund> findAll() {
        String sql = "SELECT r.id, r.booking_id, r.refund_amount_lkr, r.reason, r.status, r.requested_at, " +
                "b.customer_name, b.customer_email, b.total_amount_lkr, " +
                "m.title AS movie_title, h.name AS hall_name, s.show_date, s.start_time " +
                "FROM refunds r " +
                "JOIN bookings b ON r.booking_id = b.id " +
                "JOIN showtimes s ON b.showtime_id = s.id " +
                "JOIN movies m ON s.movie_id = m.id " +
                "JOIN halls h ON s.hall_id = h.id " +
                "ORDER BY r.id DESC";
        return jdbcTemplate.query(sql, refundRowMapper);
    }

    public Optional<Refund> findById(Long id) {
        String sql = "SELECT r.id, r.booking_id, r.refund_amount_lkr, r.reason, r.status, r.requested_at, " +
                "b.customer_name, b.customer_email, b.total_amount_lkr, " +
                "m.title AS movie_title, h.name AS hall_name, s.show_date, s.start_time " +
                "FROM refunds r " +
                "JOIN bookings b ON r.booking_id = b.id " +
                "JOIN showtimes s ON b.showtime_id = s.id " +
                "JOIN movies m ON s.movie_id = m.id " +
                "JOIN halls h ON s.hall_id = h.id " +
                "WHERE r.id = ?";
        List<Refund> list = jdbcTemplate.query(sql, refundRowMapper, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public List<Refund> findByBookingId(Long bookingId) {
        String sql = "SELECT r.id, r.booking_id, r.refund_amount_lkr, r.reason, r.status, r.requested_at, " +
                "b.customer_name, b.customer_email, b.total_amount_lkr, " +
                "m.title AS movie_title, h.name AS hall_name, s.show_date, s.start_time " +
                "FROM refunds r " +
                "JOIN bookings b ON r.booking_id = b.id " +
                "JOIN showtimes s ON b.showtime_id = s.id " +
                "JOIN movies m ON s.movie_id = m.id " +
                "JOIN halls h ON s.hall_id = h.id " +
                "WHERE r.booking_id = ?";
        return jdbcTemplate.query(sql, refundRowMapper, bookingId);
    }

    public Refund save(Refund r) {
        if (r.getId() == null) {
            String sql = "INSERT INTO refunds (booking_id, refund_amount_lkr, reason, status, requested_at) VALUES (?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"ID"});
                ps.setLong(1, r.getBookingId());
                ps.setDouble(2, r.getRefundAmountLkr());
                ps.setString(3, r.getReason());
                ps.setString(4, r.getStatus() != null ? r.getStatus().toUpperCase() : "PENDING");
                ps.setTimestamp(5, r.getRequestedAt() != null ? Timestamp.valueOf(r.getRequestedAt()) : new Timestamp(System.currentTimeMillis()));
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
                r.setId(key.longValue());
            }
            return r;
        } else {
            String sql = "UPDATE refunds SET booking_id = ?, refund_amount_lkr = ?, reason = ?, status = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    r.getBookingId(),
                    r.getRefundAmountLkr(),
                    r.getReason(),
                    r.getStatus() != null ? r.getStatus().toUpperCase() : "PENDING",
                    r.getId()
            );
            return r;
        }
    }

    public int updateStatus(Long id, String status) {
        String sql = "UPDATE refunds SET status = ? WHERE id = ?";
        return jdbcTemplate.update(sql, status.toUpperCase(), id);
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM refunds WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
