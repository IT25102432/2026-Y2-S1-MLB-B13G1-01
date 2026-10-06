package com.cinema.movie_reservation_system.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller providing real-time dashboard analytics and statistics across all 6 modules.
 */
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final JdbcTemplate jdbcTemplate;

    public DashboardController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Seat Statistics across all cinema halls
        Integer totalCapacity = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(total_rows * seats_per_row), 0) FROM halls", Integer.class
        );
        Integer totalSeats = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM seats", Integer.class
        );
        Integer availableSeats = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM seats WHERE is_active = true", Integer.class
        );
        Integer maintenanceSeats = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM seats WHERE is_active = false", Integer.class
        );
        Integer vipCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM seats WHERE UPPER(seat_type) = 'VIP'", Integer.class
        );
        Integer standardCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM seats WHERE UPPER(seat_type) = 'STANDARD'", Integer.class
        );

        // Module Counts
        Integer totalHalls = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM halls", Integer.class);
        Integer totalMovies = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM movies", Integer.class);
        Integer activeMovies = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM movies WHERE UPPER(status) = 'ACTIVE'", Integer.class);
        Integer totalShowtimes = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM showtimes", Integer.class);
        Integer totalBookings = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM bookings", Integer.class);
        Integer activeBookings = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM bookings WHERE UPPER(status) = 'CONFIRMED'", Integer.class);
        Integer totalRefunds = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM refunds", Integer.class);
        Integer pendingRefunds = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM refunds WHERE UPPER(status) = 'PENDING'", Integer.class);
        Integer totalVouchers = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vouchers", Integer.class);
        Integer activeVouchers = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vouchers WHERE is_active = true", Integer.class);

        // Put stats
        stats.put("totalCapacity", totalCapacity != null && totalCapacity > 0 ? totalCapacity : totalSeats);
        stats.put("availableSeats", availableSeats != null ? availableSeats : 0);
        stats.put("underMaintenanceSeats", maintenanceSeats != null ? maintenanceSeats : 0);
        stats.put("vipCount", vipCount != null ? vipCount : 0);
        stats.put("standardCount", standardCount != null ? standardCount : 0);

        stats.put("totalHalls", totalHalls != null ? totalHalls : 0);
        stats.put("totalMovies", totalMovies != null ? totalMovies : 0);
        stats.put("activeMovies", activeMovies != null ? activeMovies : 0);
        stats.put("totalShowtimes", totalShowtimes != null ? totalShowtimes : 0);
        stats.put("totalBookings", totalBookings != null ? totalBookings : 0);
        stats.put("activeBookings", activeBookings != null ? activeBookings : 0);
        stats.put("totalRefunds", totalRefunds != null ? totalRefunds : 0);
        stats.put("pendingRefunds", pendingRefunds != null ? pendingRefunds : 0);
        stats.put("totalVouchers", totalVouchers != null ? totalVouchers : 0);
        stats.put("activeVouchers", activeVouchers != null ? activeVouchers : 0);

        return ResponseEntity.ok(stats);
    }
}
