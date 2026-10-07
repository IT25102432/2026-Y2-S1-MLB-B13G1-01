package com.movie.reservation.service;

import com.movie.reservation.model.CinemaHall;
import com.movie.reservation.model.Seat;
import com.movie.reservation.repository.CinemaHallRepository;
import com.movie.reservation.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HallService {

    private final CinemaHallRepository hallRepository;
    private final SeatRepository seatRepository;

    public HallService(
            CinemaHallRepository hallRepository,
            SeatRepository seatRepository
    ) {
        this.hallRepository = hallRepository;
        this.seatRepository = seatRepository;
    }

    @Transactional
    public CinemaHall createHall(CinemaHall hall) {

        if (hallRepository.existsByHallNameIgnoreCase(hall.getHallName())) {
            throw new RuntimeException("Hall name already exists");
        }

        int capacity = hall.getRowsCount() * hall.getColumnsCount();

        if (capacity > 500) {
            throw new RuntimeException("Hall capacity cannot exceed 500 seats");
        }

        hall.setCapacity(capacity);

        CinemaHall savedHall = hallRepository.save(hall);

        generateSeats(savedHall);

        return savedHall;
    }

    private void generateSeats(CinemaHall hall) {

        for (int row = 0; row < hall.getRowsCount(); row++) {

            String rowLabel = String.valueOf((char) ('A' + row));

            for (int seatNumber = 1;
                 seatNumber <= hall.getColumnsCount();
                 seatNumber++) {

                Seat seat = new Seat();

                seat.setHall(hall);
                seat.setRowLabel(rowLabel);
                seat.setSeatNumber(seatNumber);
                seat.setSeatCode(rowLabel + seatNumber);

                seat.setSeatType("STANDARD");
                seat.setStatus("AVAILABLE");

                seatRepository.save(seat);
            }
        }
    }

    public List<CinemaHall> getAllHalls() {
        return hallRepository.findAll();
    }

    public CinemaHall getHallById(Long id) {
        return hallRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("Cinema hall not found")
                );
    }

    public List<Seat> getSeatsByHall(Long hallId) {

        getHallById(hallId);

        return seatRepository
                .findByHallIdOrderByRowLabelAscSeatNumberAsc(hallId);
    }

    @Transactional
    public CinemaHall updateHall(
            Long id,
            CinemaHall updatedHall
    ) {

        CinemaHall existingHall = getHallById(id);

        if (!existingHall.getHallName()
                .equalsIgnoreCase(updatedHall.getHallName())
                && hallRepository
                .existsByHallNameIgnoreCase(updatedHall.getHallName())) {

            throw new RuntimeException("Hall name already exists");
        }

        int newCapacity =
                updatedHall.getRowsCount()
                * updatedHall.getColumnsCount();

        if (newCapacity > 500) {
            throw new RuntimeException(
                    "Hall capacity cannot exceed 500 seats"
            );
        }

        boolean layoutChanged =
                !existingHall.getRowsCount()
                .equals(updatedHall.getRowsCount())
                ||
                !existingHall.getColumnsCount()
                .equals(updatedHall.getColumnsCount());

        existingHall.setHallName(
                updatedHall.getHallName()
        );

        existingHall.setRowsCount(
                updatedHall.getRowsCount()
        );

        existingHall.setColumnsCount(
                updatedHall.getColumnsCount()
        );

        existingHall.setCapacity(newCapacity);

        existingHall.setActive(
                updatedHall.getActive()
        );

        CinemaHall savedHall =
                hallRepository.save(existingHall);

        if (layoutChanged) {

            List<Seat> currentSeats =
                    seatRepository
                    .findByHallIdOrderByRowLabelAscSeatNumberAsc(id);

            boolean hasUnavailableSeat =
                    currentSeats.stream()
                    .anyMatch(seat ->
                            !"AVAILABLE"
                            .equalsIgnoreCase(seat.getStatus())
                    );

            if (hasUnavailableSeat) {
                throw new RuntimeException(
                        "Cannot change hall layout because some seats are not available"
                );
            }

            seatRepository.deleteByHallId(id);

            generateSeats(savedHall);
        }

        return savedHall;
    }

    public Seat updateSeat(
            Long seatId,
            String seatType,
            String status
    ) {

        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(
                        () -> new RuntimeException("Seat not found")
                );

        List<String> validSeatTypes =
                List.of(
                        "STANDARD",
                        "PREMIUM",
                        "RECLINER"
                );

        List<String> validStatuses =
                List.of(
                        "AVAILABLE",
                        "HELD",
                        "BOOKED",
                        "OUT_OF_ORDER"
                );

        if (!validSeatTypes.contains(
                seatType.toUpperCase())) {

            throw new RuntimeException(
                    "Invalid seat type"
            );
        }

        if (!validStatuses.contains(
                status.toUpperCase())) {

            throw new RuntimeException(
                    "Invalid seat status"
            );
        }

        if ("BOOKED".equalsIgnoreCase(seat.getStatus())
                && !"BOOKED".equalsIgnoreCase(status)) {

            throw new RuntimeException(
                    "Booked seat status cannot be changed directly"
            );
        }

        seat.setSeatType(
                seatType.toUpperCase()
        );

        seat.setStatus(
                status.toUpperCase()
        );

        return seatRepository.save(seat);
    }

    @Transactional
    public void deleteHall(Long id) {

        CinemaHall hall = getHallById(id);

        List<Seat> seats =
                seatRepository
                .findByHallIdOrderByRowLabelAscSeatNumberAsc(id);

        boolean bookedSeatExists =
                seats.stream()
                .anyMatch(seat ->
                        "BOOKED"
                        .equalsIgnoreCase(seat.getStatus())
                );

        if (bookedSeatExists) {
            throw new RuntimeException(
                    "Cannot delete hall because booked seats exist"
            );
        }

        seatRepository.deleteByHallId(id);

        hallRepository.delete(hall);
    }
}