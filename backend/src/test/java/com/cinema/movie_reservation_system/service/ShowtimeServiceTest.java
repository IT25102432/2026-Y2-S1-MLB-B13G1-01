package com.cinema.movie_reservation_system.service;

import com.cinema.movie_reservation_system.model.Showtime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ShowtimeServiceTest {

    @Autowired
    private ShowtimeService showtimeService;

    @Test
    void testInitialShowtimesLoaded() {
        List<Showtime> showtimes = showtimeService.getAllShowtimes();
        assertNotNull(showtimes);
        assertFalse(showtimes.isEmpty());
    }

    @Test
    void testCreateShowtimeSuccess() {
        Showtime showtime = new Showtime(1L, 1L, LocalDate.of(2026, 12, 1), LocalTime.of(10, 0));
        Showtime created = showtimeService.createShowtime(showtime);
        assertNotNull(created.getId());
        assertEquals(1L, created.getMovieId());
    }

    @Test
    void testOverlapCollisionProtection() {
        // Schedule first movie at 15:00 on Hall 3 (Avatar is 192 mins + 15 min buffer = ~3.5 hours)
        LocalDate date = LocalDate.of(2026, 12, 10);
        Showtime first = new Showtime(1L, 3L, date, LocalTime.of(15, 0));
        showtimeService.createShowtime(first);

        // Schedule second movie overlapping at 16:00 in same hall -> MUST throw IllegalStateException
        Showtime overlapping = new Showtime(2L, 3L, date, LocalTime.of(16, 0));
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> showtimeService.createShowtime(overlapping));
        assertTrue(ex.getMessage().contains("conflict") || ex.getMessage().contains("overlaps"));
    }

    @Test
    void testPastDateShowtimeThrows() {
        LocalDate pastDate = LocalDate.now().minusDays(2);
        Showtime pastShowtime = new Showtime(1L, 1L, pastDate, LocalTime.of(10, 0));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> showtimeService.createShowtime(pastShowtime));
        assertTrue(ex.getMessage().contains("past"));
    }
}
