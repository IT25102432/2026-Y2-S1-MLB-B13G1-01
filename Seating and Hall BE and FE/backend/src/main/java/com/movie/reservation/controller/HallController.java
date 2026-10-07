package com.movie.reservation.controller;

import com.movie.reservation.model.CinemaHall;
import com.movie.reservation.model.Seat;
import com.movie.reservation.service.HallService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/halls")
@CrossOrigin(origins = "http://localhost:5173")
public class HallController {

    private final HallService hallService;

    public HallController(HallService hallService) {
        this.hallService = hallService;
    }

    @PostMapping
    public ResponseEntity<?> createHall(
            @Valid @RequestBody CinemaHall hall
    ) {

        try {

            return ResponseEntity.ok(
                    hallService.createHall(hall)
            );

        } catch (RuntimeException ex) {

            return ResponseEntity
                    .badRequest()
                    .body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<CinemaHall>>
    getAllHalls() {

        return ResponseEntity.ok(
                hallService.getAllHalls()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getHall(
            @PathVariable Long id
    ) {

        try {

            return ResponseEntity.ok(
                    hallService.getHallById(id)
            );

        } catch (RuntimeException ex) {

            return ResponseEntity
                    .badRequest()
                    .body(ex.getMessage());
        }
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<?> getSeats(
            @PathVariable Long id
    ) {

        try {

            return ResponseEntity.ok(
                    hallService.getSeatsByHall(id)
            );

        } catch (RuntimeException ex) {

            return ResponseEntity
                    .badRequest()
                    .body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateHall(
            @PathVariable Long id,
            @Valid @RequestBody CinemaHall hall
    ) {

        try {

            return ResponseEntity.ok(
                    hallService.updateHall(id, hall)
            );

        } catch (RuntimeException ex) {

            return ResponseEntity
                    .badRequest()
                    .body(ex.getMessage());
        }
    }

    @PutMapping("/seats/{seatId}")
    public ResponseEntity<?> updateSeat(
            @PathVariable Long seatId,
            @RequestBody Map<String, String> request
    ) {

        try {

            String seatType =
                    request.get("seatType");

            String status =
                    request.get("status");

            if (seatType == null
                    || seatType.isBlank()) {

                return ResponseEntity
                        .badRequest()
                        .body("Seat type is required");
            }

            if (status == null
                    || status.isBlank()) {

                return ResponseEntity
                        .badRequest()
                        .body("Seat status is required");
            }

            Seat seat =
                    hallService.updateSeat(
                            seatId,
                            seatType,
                            status
                    );

            return ResponseEntity.ok(seat);

        } catch (RuntimeException ex) {

            return ResponseEntity
                    .badRequest()
                    .body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHall(
            @PathVariable Long id
    ) {

        try {

            hallService.deleteHall(id);

            return ResponseEntity.ok(
                    "Cinema hall deleted successfully"
            );

        } catch (RuntimeException ex) {

            return ResponseEntity
                    .badRequest()
                    .body(ex.getMessage());
        }
    }
}