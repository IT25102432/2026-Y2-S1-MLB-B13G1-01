package com.cinema.movie_reservation_system.controller;

import com.cinema.movie_reservation_system.model.Hall;
import com.cinema.movie_reservation_system.model.Seat;
import com.cinema.movie_reservation_system.model.Showtime;
import com.cinema.movie_reservation_system.service.HallService;
import com.cinema.movie_reservation_system.service.ShowtimeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST Controller for Showtime Scheduling & Theater Assignment.
 * Part of Showtime Scheduling & Theater Assignment (IT25103071).
 */
@RestController
@RequestMapping("/api/showtimes")
@CrossOrigin(origins = "*")
public class ShowtimeController {

    private final ShowtimeService showtimeService;
    private final HallService hallService;
    private final JdbcTemplate jdbcTemplate;

    public ShowtimeController(ShowtimeService showtimeService, HallService hallService, JdbcTemplate jdbcTemplate) {
        this.showtimeService = showtimeService;
        this.hallService = hallService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public ResponseEntity<List<Showtime>> getAllShowtimes() {
        return ResponseEntity.ok(showtimeService.getAllShowtimes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getShowtimeById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(showtimeService.getShowtimeById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createShowtime(@RequestBody Showtime showtime) {
        try {
            Showtime created = showtimeService.createShowtime(showtime);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create showtime", "details", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateShowtime(@PathVariable Long id, @RequestBody Showtime showtime) {
        try {
            Showtime updated = showtimeService.updateShowtime(id, showtime);
            return ResponseEntity.ok(updated);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update showtime", "details", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShowtime(@PathVariable Long id) {
        try {
            showtimeService.deleteShowtime(id);
            return ResponseEntity.ok(Map.of("message", "Showtime #" + id + " was deleted successfully.", "id", id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete showtime", "details", e.getMessage()));
        }
    }

    /**
     * Retrieves seat matrix for this showtime with real-time status:
     * 'BOOKED', 'MAINTENANCE', or 'AVAILABLE', along with seat price in LKR.
     */
    @GetMapping("/{id}/seats")
    public ResponseEntity<?> getShowtimeSeats(@PathVariable Long id) {
        try {
            Showtime showtime = showtimeService.getShowtimeById(id);
            Hall hall = hallService.getHallById(showtime.getHallId());
            List<Seat> hallSeats = hallService.getSeatsByHallId(showtime.getHallId());

            // Query booked seat IDs for confirmed bookings
            String bookedSeatsSql = "SELECT bs.seat_id FROM booking_seats bs " +
                    "JOIN bookings b ON bs.booking_id = b.id " +
                    "WHERE b.showtime_id = ? AND UPPER(b.status) = 'CONFIRMED'";
            Set<Long> bookedSeatIds = new HashSet<>(jdbcTemplate.query(
                    bookedSeatsSql, (rs, rowNum) -> rs.getLong("seat_id"), id
            ));

            List<Map<String, Object>> annotatedSeats = new ArrayList<>();
            for (Seat seat : hallSeats) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", seat.getId());
                map.put("hallId", seat.getHallId());
                map.put("seatRow", seat.getSeatRow());
                map.put("seatNumber", seat.getSeatNumber());
                map.put("seatLabel", seat.getSeatLabel());
                map.put("seatType", seat.getSeatType());
                map.put("isActive", seat.isActive());

                // Pricing: VIP = Rs. 2,000, Standard = hall base_price (default Rs. 1,200)
                double price = "VIP".equalsIgnoreCase(seat.getSeatType())
                        ? 2000.00
                        : (hall.getBasePrice() != null ? hall.getBasePrice() : 1200.00);
                map.put("priceLkr", price);

                if (!seat.isActive()) {
                    map.put("status", "MAINTENANCE");
                } else if (bookedSeatIds.contains(seat.getId())) {
                    map.put("status", "BOOKED");
                } else {
                    map.put("status", "AVAILABLE");
                }

                annotatedSeats.add(map);
            }

            return ResponseEntity.ok(Map.of(
                    "showtime", showtime,
                    "hall", hall,
                    "seats", annotatedSeats
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
