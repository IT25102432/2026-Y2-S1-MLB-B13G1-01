package com.cinema.movie_reservation_system.service;

import com.cinema.movie_reservation_system.model.Hall;
import com.cinema.movie_reservation_system.model.Seat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class HallServiceTest {

    @Autowired
    private HallService hallService;

    @Test
    void testInitialSeedDataLoaded() {
        // Verify seed hall "Hall 1 - IMAX" from schema.sql
        List<Hall> halls = hallService.getAllHalls();
        assertFalse(halls.isEmpty(), "Halls should not be empty after schema.sql initialization");

        Hall hall1 = halls.stream()
                .filter(h -> "Hall 1 - IMAX".equals(h.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(hall1, "Hall 1 - IMAX should exist");
        assertEquals(5, hall1.getTotalRows());
        assertEquals(8, hall1.getSeatsPerRow());
        assertEquals("IMAX", hall1.getHallType());
        assertEquals(40, hall1.getTotalCapacity());

        // Verify seed seats for Hall 1 (5 rows * 8 seats = 40 seats)
        List<Seat> seats = hallService.getSeatsByHallId(hall1.getId());
        assertEquals(40, seats.size(), "Hall 1 should have 40 seats");

        // Verify Row A Seat 1 is VIP and active
        Seat seatA1 = seats.stream()
                .filter(s -> "A".equals(s.getSeatRow()) && s.getSeatNumber() == 1)
                .findFirst()
                .orElse(null);
        assertNotNull(seatA1);
        assertEquals("VIP", seatA1.getSeatType());
        assertTrue(seatA1.isActive());

        // Verify Row C Seat 1 is STANDARD and active
        Seat seatC1 = seats.stream()
                .filter(s -> "C".equals(s.getSeatRow()) && s.getSeatNumber() == 1)
                .findFirst()
                .orElse(null);
        assertNotNull(seatC1);
        assertEquals("STANDARD", seatC1.getSeatType());
        assertTrue(seatC1.isActive());
    }

    @Test
    void testAllocateNewHallAndAutoGenerateSeats() {
        Hall newHall = new Hall("Test Hall 3", 3, 5, "STANDARD");
        Hall created = hallService.allocateHall(newHall);

        assertNotNull(created.getId());
        assertEquals("Test Hall 3", created.getName());

        // 3 rows (A, B, C) * 5 seats = 15 seats
        List<Seat> seats = hallService.getSeatsByHallId(created.getId());
        assertEquals(15, seats.size());

        // Verify row letters
        assertTrue(seats.stream().anyMatch(s -> "A".equals(s.getSeatRow())));
        assertTrue(seats.stream().anyMatch(s -> "B".equals(s.getSeatRow())));
        assertTrue(seats.stream().anyMatch(s -> "C".equals(s.getSeatRow())));
    }

    @Test
    void testToggleSeatStatus() {
        List<Seat> seats = hallService.getSeatsByHallId(1L);
        Seat seat = seats.get(0);
        boolean initialStatus = seat.isActive();

        // Toggle status
        Seat toggled = hallService.toggleSeatStatus(seat.getId());
        assertEquals(!initialStatus, toggled.isActive());

        // Toggle back
        Seat reverted = hallService.toggleSeatStatus(seat.getId());
        assertEquals(initialStatus, reverted.isActive());
    }

    @Test
    void testAllocateHallWithBasePrice() {
        Hall newHall = new Hall("Hall With Price", 2, 4, "IMAX", 1800.0);
        Hall created = hallService.allocateHall(newHall);

        assertNotNull(created.getId());
        assertEquals("Hall With Price", created.getName());
        assertEquals(1800.0, created.getBasePrice());

        Hall retrieved = hallService.getHallById(created.getId());
        assertEquals(1800.0, retrieved.getBasePrice());
    }

    @Test
    void testUpdateHall() {
        Hall newHall = new Hall("Hall Before Update", 2, 3, "STANDARD", 1000.0);
        Hall created = hallService.allocateHall(newHall);

        Hall updateData = new Hall();
        updateData.setName("Hall After Update");
        updateData.setHallType("VIP");
        updateData.setBasePrice(2500.0);

        Hall updated = hallService.updateHall(created.getId(), updateData);
        assertEquals("Hall After Update", updated.getName());
        assertEquals("VIP", updated.getHallType());
        assertEquals(2500.0, updated.getBasePrice());
    }

    @Test
    void testDeleteHall() {
        Hall newHall = new Hall("Hall To Delete In Service", 2, 2, "STANDARD");
        Hall created = hallService.allocateHall(newHall);
        Long hallId = created.getId();

        // Ensure seats exist
        List<Seat> seatsBefore = hallService.getSeatsByHallId(hallId);
        assertFalse(seatsBefore.isEmpty());

        // Delete hall
        hallService.deleteHall(hallId);

        // Verify hall is deleted
        assertThrows(IllegalArgumentException.class, () -> hallService.getHallById(hallId));

        // Verify seats cascade deleted
        assertThrows(IllegalArgumentException.class, () -> hallService.getSeatsByHallId(hallId));
    }
}
