package com.cinema.movie_reservation_system.controller;

import com.cinema.movie_reservation_system.model.Hall;
import com.cinema.movie_reservation_system.model.Seat;
import com.cinema.movie_reservation_system.service.HallService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class HallControllerTest {

    @Autowired
    private HallService hallService;

    private HallController hallController;

    @BeforeEach
    void setUp() {
        hallController = new HallController(hallService);
    }

    @Test
    void testGetAllHalls() {
        ResponseEntity<List<Hall>> response = hallController.getAllHalls();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void testGetHallById() {
        ResponseEntity<?> response = hallController.getHallById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Hall hall = (Hall) response.getBody();
        assertNotNull(hall);
        assertEquals("Hall 1 - IMAX", hall.getName());
    }

    @Test
    void testGetHallById_NotFound() {
        ResponseEntity<?> response = hallController.getHallById(99999L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetSeatsByHallId() {
        ResponseEntity<?> response = hallController.getSeatsByHallId(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        List<Seat> seats = (List<Seat>) response.getBody();
        assertNotNull(seats);
        assertEquals(40, seats.size());
    }

    @Test
    void testCreateHall() {
        Hall newHall = new Hall("Hall Controller Test", 2, 4, "STANDARD");
        ResponseEntity<?> response = hallController.createHall(newHall);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Hall created = (Hall) response.getBody();
        assertNotNull(created);
        assertNotNull(created.getId());
    }

    @Test
    void testUpdateSeatStatus() {
        // Toggle without body
        ResponseEntity<?> responseToggle = hallController.updateSeatStatus(1L, null);
        assertEquals(HttpStatus.OK, responseToggle.getStatusCode());
        Seat toggledSeat = (Seat) responseToggle.getBody();
        assertNotNull(toggledSeat);

        // Explicit set
        ResponseEntity<?> responseExplicit = hallController.updateSeatStatus(1L, Map.of("isActive", true));
        assertEquals(HttpStatus.OK, responseExplicit.getStatusCode());
        Seat explicitSeat = (Seat) responseExplicit.getBody();
        assertNotNull(explicitSeat);
        assertTrue(explicitSeat.isActive());
    }
}
