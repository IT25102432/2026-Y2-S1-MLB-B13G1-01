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
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid cinema hall ID: " + id);
        }
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
        getHallById(hallId);
        return seatRepository.findByHallId(hallId);
    }

    /**
     * Allocate and create a new cinema hall and automatically generate its seat matrix layout.
     * Server-side Constraints: Name non-blank, total_rows (1–26), seats_per_row (1–30), base_price > 0 LKR.
     *
     * @param hall The hall details.
     * @return The created Hall object.
     */
    @Transactional
    public Hall allocateHall(Hall hall) {
        validateHallConstraints(hall);

        if (hallRepository.existsByNameIgnoreCase(hall.getName())) {
            throw new IllegalArgumentException("Hall name already exists");
        }

        // 1. Save the hall entity to get generated primary key ID
        Hall savedHall = hallRepository.save(hall);

        // 2. Auto-generate the seating matrix (Rows A-Z)
        generateSeatMatrix(savedHall);

        return savedHall;
    }

    /**
     * Update an existing cinema hall's details.
     *
     * @param id          The ID of the hall to update.
     * @param updatedHall The updated hall data.
     * @return The updated Hall object.
     */
    @Transactional
    public Hall updateHall(Long id, Hall updatedHall) {
        Hall existing = getHallById(id);

        if (updatedHall.getName() != null) {
            String trimmedName = updatedHall.getName().trim();
            if (trimmedName.isEmpty()) {
                throw new IllegalArgumentException("Hall name cannot be empty.");
            }
            if (hallRepository.existsByNameIgnoreCaseAndIdNot(trimmedName, id)) {
                throw new IllegalArgumentException("Hall name already exists");
            }
            existing.setName(trimmedName);
        }
        if (updatedHall.getHallType() != null && !updatedHall.getHallType().trim().isEmpty()) {
            existing.setHallType(updatedHall.getHallType().trim());
        }
        if (updatedHall.getTotalRows() > 0) {
            if (updatedHall.getTotalRows() > 26) {
                throw new IllegalArgumentException("Total rows must be between 1 and 26.");
            }
            existing.setTotalRows(updatedHall.getTotalRows());
        }
        if (updatedHall.getSeatsPerRow() > 0) {
            if (updatedHall.getSeatsPerRow() > 30) {
                throw new IllegalArgumentException("Seats per row must be between 1 and 30.");
            }
            existing.setSeatsPerRow(updatedHall.getSeatsPerRow());
        }
        if (updatedHall.getBasePrice() != null) {
            if (updatedHall.getBasePrice() <= 0) {
                throw new IllegalArgumentException("Base price must be greater than 0 LKR.");
            }
            existing.setBasePrice(updatedHall.getBasePrice());
        }

        return hallRepository.save(existing);
    }

    /**
     * Delete a cinema hall and cascade delete all its associated seats.
     *
     * @param id The hall ID to delete.
     */
    @Transactional
    public void deleteHall(Long id) {
        getHallById(id);
        seatRepository.deleteByHallId(id);
        hallRepository.deleteById(id);
    }

    /**
     * Auto-generate seat matrices (Rows A-Z, Seat numbers 1..N)
     * VIP rows priced at Rs. 2,000, Standard at Rs. 1,200.
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

            // Determine seat type: front 2 rows are VIP (Rs. 2,000) or all VIP if hall is VIP
            String seatType = "STANDARD";
            if ("VIP".equalsIgnoreCase(hall.getHallType()) || r < 2) {
                seatType = "VIP";
            }

            for (int s = 1; s <= cols; s++) {
                Seat seat = new Seat(
                        hall.getId(),
                        rowLabel,
                        s,
                        seatType,
                        true // active by default
                );
                generatedSeats.add(seat);
            }
        }

        seatRepository.saveAll(generatedSeats);
        return generatedSeats;
    }

    /**
     * Toggle the active status of a seat (Admin maintenance toggle).
     *
     * @param seatId The seat ID.
     * @return The updated Seat object.
     */
    @Transactional
    public Seat toggleSeatStatus(Long seatId) {
        if (seatId == null || seatId <= 0) {
            throw new IllegalArgumentException("Invalid seat ID: " + seatId);
        }
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
     * @param seatId   The seat ID.
     * @param isActive Desired active state.
     * @return The updated Seat object.
     */
    @Transactional
    public Seat updateSeatStatus(Long seatId, boolean isActive) {
        if (seatId == null || seatId <= 0) {
            throw new IllegalArgumentException("Invalid seat ID: " + seatId);
        }
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("Seat not found with ID: " + seatId));

        seatRepository.updateSeatStatus(seatId, isActive);
        seat.setActive(isActive);
        return seat;
    }

    /**
     * Server-side constraint validator for Hall.
     */
    private void validateHallConstraints(Hall hall) {
        if (hall == null) {
            throw new IllegalArgumentException("Hall data cannot be null.");
        }
        if (hall.getName() == null || hall.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Hall name cannot be empty.");
        }
        if (hall.getTotalRows() < 1 || hall.getTotalRows() > 26) {
            throw new IllegalArgumentException("Total rows must be between 1 and 26.");
        }
        if (hall.getSeatsPerRow() < 1 || hall.getSeatsPerRow() > 30) {
            throw new IllegalArgumentException("Seats per row must be between 1 and 30.");
        }
        if (hall.getHallType() == null || hall.getHallType().trim().isEmpty()) {
            hall.setHallType("STANDARD");
        }
        if (hall.getBasePrice() == null || hall.getBasePrice() <= 0) {
            throw new IllegalArgumentException("Base price must be greater than 0 LKR.");
        }
    }

    private String getRowLabel(int rowIndex) {
        if (rowIndex < 26) {
            return String.valueOf((char) ('A' + rowIndex));
        }
        int firstChar = (rowIndex / 26) - 1;
        int secondChar = rowIndex % 26;
        return String.valueOf((char) ('A' + firstChar)) + (char) ('A' + secondChar);
    }
}
