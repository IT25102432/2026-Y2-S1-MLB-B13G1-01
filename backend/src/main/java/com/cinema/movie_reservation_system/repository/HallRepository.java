package com.cinema.movie_reservation_system.repository;

import com.cinema.movie_reservation_system.model.Hall;
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
 * Repository for managing Hall persistence using Spring JdbcTemplate.
 * Part of Seating Layout and Hall Allocation (IT25102154).
 */
@Repository
public class HallRepository {

    private final JdbcTemplate jdbcTemplate;

    public HallRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper to map database rows to Hall instances
    private final RowMapper<Hall> hallRowMapper = (rs, rowNum) -> {
        Hall hall = new Hall(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getInt("total_rows"),
                rs.getInt("seats_per_row"),
                rs.getString("hall_type")
        );
        try {
            double bp = rs.getDouble("base_price");
            if (!rs.wasNull()) {
                hall.setBasePrice(bp);
            } else {
                hall.setBasePrice(1200.0);
            }
        } catch (Exception e) {
            hall.setBasePrice(1200.0);
        }
        return hall;
    };

    /**
     * Retrieve all cinema halls from the database.
     *
     * @return List of all halls.
     */
    public List<Hall> findAll() {
        String sql = "SELECT id, name, total_rows, seats_per_row, hall_type, base_price FROM halls ORDER BY id ASC";
        return jdbcTemplate.query(sql, hallRowMapper);
    }

    /**
     * Find a cinema hall by its unique ID.
     *
     * @param id The hall ID.
     * @return Optional containing the Hall if found, or empty Optional.
     */
    public Optional<Hall> findById(Long id) {
        String sql = "SELECT id, name, total_rows, seats_per_row, hall_type, base_price FROM halls WHERE id = ?";
        List<Hall> halls = jdbcTemplate.query(sql, hallRowMapper, id);
        return halls.isEmpty() ? Optional.empty() : Optional.of(halls.get(0));
    }

    /**
     * Save or update a cinema hall in the database.
     * If the ID is null, a new record is inserted and its generated ID is assigned.
     * If the ID is present, the existing record is updated.
     *
     * @param hall The hall to persist.
     * @return The persisted Hall with its assigned ID.
     */
    public Hall save(Hall hall) {
        if (hall.getBasePrice() == null || hall.getBasePrice() <= 0) {
            hall.setBasePrice(1200.0);
        }
        if (hall.getId() == null) {
            String sql = "INSERT INTO halls (name, total_rows, seats_per_row, hall_type, base_price) VALUES (?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, hall.getName());
                ps.setInt(2, hall.getTotalRows());
                ps.setInt(3, hall.getSeatsPerRow());
                ps.setString(4, hall.getHallType());
                ps.setDouble(5, hall.getBasePrice());
                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();
            if (key != null) {
                hall.setId(key.longValue());
            }
            return hall;
        } else {
            String sql = "UPDATE halls SET name = ?, total_rows = ?, seats_per_row = ?, hall_type = ?, base_price = ? WHERE id = ?";
            jdbcTemplate.update(sql, hall.getName(), hall.getTotalRows(), hall.getSeatsPerRow(), hall.getHallType(), hall.getBasePrice(), hall.getId());
            return hall;
        }
    }

    /**
     * Delete a cinema hall by its unique ID.
     *
     * @param id The hall ID to delete.
     * @return Number of rows affected.
     */
    public int deleteById(Long id) {
        String sql = "DELETE FROM halls WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
