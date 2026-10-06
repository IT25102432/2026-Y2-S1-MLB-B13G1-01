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
@RequestMapping("/api/halls")
@CrossOrigin(origins = "*")
public class HallController {

    private final HallService hallService;

    public HallController(HallService hallService) {
        this.hallService = hallService;
    }

    /**
     * [READ] GET /api/halls
     * Retrieve a list of all cinema halls.
     *
     * @return 200 OK with list of halls.
     */
    @GetMapping
    public ResponseEntity<List<Hall>> getAllHalls() {
        List<Hall> halls = hallService.getAllHalls();
        return ResponseEntity.ok(halls);
    }

    /**
     * [READ] GET /api/halls/{id}
     * Retrieve details of a specific cinema hall by ID.
     *
     * @param id The hall ID.
     * @return 200 OK with Hall details, or 404 NOT FOUND if nonexistent.
     */
    @GetMapping("/{id}")
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
     * Retrieve all seats / seating layout for a specific hall.
     *
     * @param id The hall ID.
     * @return 200 OK with list of Seat objects, or 404 NOT FOUND if hall nonexistent.
     */
    @GetMapping("/{id}/seats")
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
     * Create a new cinema hall and auto-generate its seating layout matrix.
     *
     * @param hall The hall details JSON payload (name, totalRows, seatsPerRow, hallType).
     * @return 201 CREATED with the newly created Hall.
     */
    @PostMapping
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
     * Update an existing cinema hall's name or hall type.
     *
     * @param id          The ID of the hall to update.
     * @param updatedHall The updated hall data.
     * @return 200 OK with updated Hall, or 404 NOT FOUND.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateHall(@PathVariable Long id, @RequestBody Hall updatedHall) {
        try {
            Hall result = hallService.updateHall(id, updatedHall);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update cinema hall", "details", e.getMessage()));
        }
    }

    /**
     * [DELETE] DELETE /api/halls/{id}
     * Delete a cinema hall and cascade delete all its associated seats.
     *
     * @param id The ID of the hall to delete.
     * @return 200 OK with confirmation message, or 404 NOT FOUND.
     */
    @DeleteMapping("/{id}")
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
     * Toggle or update the active status of a specific seat (maintenance vs available).
     * If request body contains {"isActive": true/false}, it sets the status explicitly.
     * If no body is provided, it toggles the existing active status.
     *
     * @param seatId  The seat ID.
     * @param payload Optional JSON map containing {"isActive": boolean}.
     * @return 200 OK with the updated Seat object, or 404 NOT FOUND if seat doesn't exist.
     */
    @PutMapping("/seats/{seatId}/status")
    public ResponseEntity<?> updateSeatStatus(
            @PathVariable Long seatId,
            @RequestBody(required = false) Map<String, Object> payload) {
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
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update seat status", "details", e.getMessage()));
        }
    }
}
