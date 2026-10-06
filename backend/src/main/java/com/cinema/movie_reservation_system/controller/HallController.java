package com.cinema.movie_reservation_system.controller;

import com.cinema.movie_reservation_system.model.Hall;
import com.cinema.movie_reservation_system.model.Seat;
import com.cinema.movie_reservation_system.service.HallService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Seating Layout and Hall Allocation.
 * Exposes full CRUD endpoints for managing cinema halls and their seating layouts.
 * Part of Seating Layout and Hall Allocation (IT25102154).
 */
@RestController
@CrossOrigin(origins = "*")
public class HallController {

    private final HallService hallService;

    public HallController(HallService hallService) {
        this.hallService = hallService;
    }

    /**
     * [READ] GET /api/halls
     */
    @GetMapping("/api/halls")
    public ResponseEntity<List<Hall>> getAllHalls() {
        List<Hall> halls = hallService.getAllHalls();
        return ResponseEntity.ok(halls);
    }

    /**
     * [READ] GET /api/halls/{id}
     */
    @GetMapping("/api/halls/{id}")
    public ResponseEntity<?> getHallById(@PathVariable Long id) {
        try {
            Hall hall = hallService.getHallById(id);
            return ResponseEntity.ok(hall);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * [READ] GET /api/halls/{id}/seats
     */
    @GetMapping("/api/halls/{id}/seats")
    public ResponseEntity<?> getSeatsByHallId(@PathVariable Long id) {
        try {
            List<Seat> seats = hallService.getSeatsByHallId(id);
            return ResponseEntity.ok(seats);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * [CREATE] POST /api/halls
     */
    @PostMapping("/api/halls")
    public ResponseEntity<?> createHall(@RequestBody Hall hall) {
        try {
            Hall createdHall = hallService.allocateHall(hall);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdHall);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create cinema hall", "details", e.getMessage()));
        }
    }

    /**
     * [UPDATE] PUT /api/halls/{id}
     */
    @PutMapping("/api/halls/{id}")
    public ResponseEntity<?> updateHall(@PathVariable Long id, @RequestBody Hall updatedHall) {
        try {
            Hall result = hallService.updateHall(id, updatedHall);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update cinema hall", "details", e.getMessage()));
        }
    }

    /**
     * [DELETE] DELETE /api/halls/{id}
     */
    @DeleteMapping("/api/halls/{id}")
    public ResponseEntity<?> deleteHall(@PathVariable Long id) {
        try {
            hallService.deleteHall(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Cinema hall #" + id + " and all its seats were deleted successfully.",
                    "id", id
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete cinema hall", "details", e.getMessage()));
        }
    }

    /**
     * [UPDATE] PUT /api/halls/seats/{seatId}/status
     */
    @PutMapping("/api/halls/seats/{seatId}/status")
    public ResponseEntity<?> updateSeatStatus(
            @PathVariable Long seatId,
            @RequestBody(required = false) Map<String, Object> payload) {
        if (seatId == null || seatId <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid seat ID: " + seatId));
        }
        try {
            Seat updatedSeat;
            if (payload != null && payload.containsKey("isActive")) {
                boolean isActive = Boolean.parseBoolean(payload.get("isActive").toString());
                updatedSeat = hallService.updateSeatStatus(seatId, isActive);
            } else {
                updatedSeat = hallService.toggleSeatStatus(seatId);
            }
            return ResponseEntity.ok(updatedSeat);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * [TOGGLE] PATCH /api/seats/{id}/toggle
     */
    @PatchMapping("/api/seats/{id}/toggle")
    public ResponseEntity<?> toggleSeatMaintenance(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid seat ID: " + id));
        }
        try {
            Seat updatedSeat = hallService.toggleSeatStatus(id);
            return ResponseEntity.ok(updatedSeat);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
