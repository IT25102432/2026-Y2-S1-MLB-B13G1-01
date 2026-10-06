package com.cinema.movie_reservation_system.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DashboardControllerTest {

    @Autowired
    private DashboardController dashboardController;

    @Test
    void testGetDashboardStats() {
        ResponseEntity<Map<String, Object>> response = dashboardController.getDashboardStats();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> stats = response.getBody();
        assertNotNull(stats);

        assertTrue(stats.containsKey("totalCapacity"));
        assertTrue(stats.containsKey("availableSeats"));
        assertTrue(stats.containsKey("underMaintenanceSeats"));
        assertTrue(stats.containsKey("vipCount"));
        assertTrue(stats.containsKey("standardCount"));

        int totalCapacity = (int) stats.get("totalCapacity");
        int vipCount = (int) stats.get("vipCount");
        int standardCount = (int) stats.get("standardCount");

        assertTrue(totalCapacity > 0);
        assertTrue(vipCount > 0);
        assertTrue(standardCount > 0);
    }
}
