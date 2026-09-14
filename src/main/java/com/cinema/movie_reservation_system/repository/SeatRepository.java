package com.cinema.movie_reservation_system.repository;

import com.cinema.movie_reservation_system.model.Seat;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing Seat persistence using Spring JdbcTemplate.
 * Part of Seating Layout and Hall Allocation (IT25102154).
 */
@Repository
public class SeatRepository {

    private final JdbcTemplate jdbcTemplate;

    public SeatRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper to map database rows to Seat instances
    private final RowMapper<Seat> seatRowMapper = (rs, rowNum) -> new Seat(
            rs.getLong("id"),
            rs.getLong("hall_id"),
            rs.getString("seat_row"),
            rs.getInt("seat_number"),
            rs.getString("seat_type"),
            rs.getBoolean("is_active")
    );

    /**
     * Find all seats for a given hall ID, ordered by row and seat number.
     *
     * @param hallId The ID of the cinema hall.
     * @return List of seats in the hall.
     */
    public List<Seat> findByHallId(Long hallId) {
        String sql = "SELECT id, hall_id, seat_row, seat_number, seat_type, is_active FROM seats WHERE hall_id = ? ORDER BY seat_row ASC, seat_number ASC";
        return jdbcTemplate.query(sql, seatRowMapper, hallId);
    }

    /**
     * Find a seat by its unique ID.
     *
     * @param seatId The seat ID.
     * @return Optional containing the Seat if found, or empty Optional.
     */
    public Optional<Seat> findById(Long seatId) {
        String sql = "SELECT id, hall_id, seat_row, seat_number, seat_type, is_active FROM seats WHERE id = ?";
        List<Seat> seats = jdbcTemplate.query(sql, seatRowMapper, seatId);
        return seats.isEmpty() ? Optional.empty() : Optional.of(seats.get(0));
    }

    /**
     * Update the active status of a seat.
     *
     * @param seatId   The ID of the seat to update.
     * @param isActive The new active status (true for available/active, false for disabled/maintenance).
     * @return The number of rows affected.
     */
    public int updateSeatStatus(Long seatId, boolean isActive) {
        String sql = "UPDATE seats SET is_active = ? WHERE id = ?";
        return jdbcTemplate.update(sql, isActive, seatId);
    }

    /**
     * Batch insert a list of seats (useful when auto-generating hall layouts).
     *
     * @param seats The list of Seat objects to persist.
     */
    public void saveAll(List<Seat> seats) {
        if (seats == null || seats.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO seats (hall_id, seat_row, seat_number, seat_type, is_active) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Seat seat = seats.get(i);
                ps.setLong(1, seat.getHallId());
                ps.setString(2, seat.getSeatRow());
                ps.setInt(3, seat.getSeatNumber());
                ps.setString(4, seat.getSeatType());
                ps.setBoolean(5, seat.isActive());
            }

            @Override
            public int getBatchSize() {
                return seats.size();
            }
        });
    }

    /**
     * Delete all seats belonging to a specific hall.
     *
     * @param hallId The hall ID.
     * @return The number of rows deleted.
     */
    public int deleteByHallId(Long hallId) {
        String sql = "DELETE FROM seats WHERE hall_id = ?";
        return jdbcTemplate.update(sql, hallId);
    }
}
