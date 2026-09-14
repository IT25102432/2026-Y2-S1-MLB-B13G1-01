package com.cinema.movie_reservation_system.service;

import com.cinema.movie_reservation_system.model.Hall;
import com.cinema.movie_reservation_system.model.Seat;
import com.cinema.movie_reservation_system.repository.HallRepository;
import com.cinema.movie_reservation_system.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service handling business logic for Cinema Halls and Seating Layout.
 * Part of Seating Layout and Hall Allocation (IT25102154).
 */
@Service
public class HallService {

    private final HallRepository hallRepository;
    private final SeatRepository seatRepository;

    public HallService(HallRepository hallRepository, SeatRepository seatRepository) {
        this.hallRepository = hallRepository;
        this.seatRepository = seatRepository;
    }

    /**
     * Retrieve all cinema halls.
     *
     * @return List of all halls.
     */
    public List<Hall> getAllHalls() {
        return hallRepository.findAll();
    }

    /**
     * Retrieve a specific hall by its ID.
     *
     * @param id The hall ID.
     * @return The Hall object.
     * @throws IllegalArgumentException if the hall is not found.
     */
    public Hall getHallById(Long id) {
        return hallRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cinema hall not found with ID: " + id));
    }

    /**
     * Retrieve all seats for a given cinema hall.
     *
     * @param hallId The hall ID.
     * @return List of seats in the hall.
     * @throws IllegalArgumentException if the hall does not exist.
     */
    public List<Seat> getSeatsByHallId(Long hallId) {
        // Validate that the hall exists first
        getHallById(hallId);
        return seatRepository.findByHallId(hallId);
    }

    /**
     * Allocate and create a new cinema hall and automatically generate its seat matrix layout.
     *
     * @param hall The hall details (name, totalRows, seatsPerRow, hallType).
     * @return The created Hall object with its generated ID.
     * @throws IllegalArgumentException if input parameters are invalid.
     */
    @Transactional
    public Hall allocateHall(Hall hall) {
        if (hall.getName() == null || hall.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Hall name cannot be empty.");
        }
        if (hall.getTotalRows() <= 0) {
            throw new IllegalArgumentException("Total rows must be greater than zero.");
        }
        if (hall.getSeatsPerRow() <= 0) {
            throw new IllegalArgumentException("Seats per row must be greater than zero.");
        }
        if (hall.getHallType() == null || hall.getHallType().trim().isEmpty()) {
            hall.setHallType("STANDARD");
        }

        // 1. Save the hall entity to get generated primary key ID
        Hall savedHall = hallRepository.save(hall);

        // 2. Auto-generate the seating matrix (Rows A-Z)
        generateSeatMatrix(savedHall);

        return savedHall;
    }

    /**
     * Helper method to auto-generate seat matrices (Rows A-Z, Seat numbers 1..N)
     * and persist them in the database for the given hall.
     *
     * @param hall The hall for which seats should be generated.
     * @return The list of generated Seat objects.
     */
    public List<Seat> generateSeatMatrix(Hall hall) {
        List<Seat> generatedSeats = new ArrayList<>();
        int rows = hall.getTotalRows();
        int cols = hall.getSeatsPerRow();

        for (int r = 0; r < rows; r++) {
            String rowLabel = getRowLabel(r);

            // Determine seat type: front 2 rows are VIP by default, or all VIP if hallType is VIP
            String seatType = "STANDARD";
            if ("VIP".equalsIgnoreCase(hall.getHallType()) || "IMAX".equalsIgnoreCase(hall.getHallType()) && r < 2) {
                seatType = "VIP";
            } else if (r < 2) {
                seatType = "VIP";
            }

            for (int s = 1; s <= cols; s++) {
                Seat seat = new Seat(
                        hall.getId(),
                        rowLabel,
                        s,
                        seatType,
                        true // newly generated seats are active by default
                );
                generatedSeats.add(seat);
            }
        }

        // Batch persist all generated seats
        seatRepository.saveAll(generatedSeats);
        return generatedSeats;
    }

    /**
     * Toggle the active status of a seat (e.g. enable/disable for maintenance or booking).
     *
     * @param seatId The ID of the seat.
     * @return The updated Seat object.
     * @throws IllegalArgumentException if seat is not found.
     */
    @Transactional
    public Seat toggleSeatStatus(Long seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("Seat not found with ID: " + seatId));

        boolean updatedStatus = !seat.isActive();
        seatRepository.updateSeatStatus(seatId, updatedStatus);
        seat.setActive(updatedStatus);
        return seat;
    }

    /**
     * Explicitly set the active status of a seat.
     *
     * @param seatId   The ID of the seat.
     * @param isActive Desired active state.
     * @return The updated Seat object.
     * @throws IllegalArgumentException if seat is not found.
     */
    @Transactional
    public Seat updateSeatStatus(Long seatId, boolean isActive) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("Seat not found with ID: " + seatId));

        seatRepository.updateSeatStatus(seatId, isActive);
        seat.setActive(isActive);
        return seat;
    }

    /**
     * Helper to convert row index (0-indexed) to letter label (A, B, C... Z, AA, AB...).
     *
     * @param rowIndex 0-based row index.
     * @return Alphabetic row string.
     */
    private String getRowLabel(int rowIndex) {
        if (rowIndex < 26) {
            return String.valueOf((char) ('A' + rowIndex));
        }
        // For larger halls beyond 26 rows (e.g. AA, AB)
        int firstChar = (rowIndex / 26) - 1;
        int secondChar = rowIndex % 26;
        return String.valueOf((char) ('A' + firstChar)) + (char) ('A' + secondChar);
    }
}
