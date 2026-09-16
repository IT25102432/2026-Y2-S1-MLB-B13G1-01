package com.ceylonstar.showtime.config;

import com.ceylonstar.showtime.model.ShowStatus;
import com.ceylonstar.showtime.model.Showtime;
import com.ceylonstar.showtime.repository.ShowtimeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(ShowtimeRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }

            LocalDate today = LocalDate.now();

            repository.save(build("Dune: Part Three", "Sci-Fi", "English", "Hall 1",
                    today, LocalTime.of(14, 30), 165, 1200.0, ShowStatus.SCHEDULED));

            repository.save(build("Sinhala Rasa Kathawa", "Drama", "Sinhala", "Hall 2",
                    today, LocalTime.of(17, 0), 128, 900.0, ShowStatus.SCHEDULED));

            repository.save(build("Galaxy Raiders", "Animation", "English", "VIP Hall",
                    today, LocalTime.of(19, 45), 110, 1800.0, ShowStatus.SCHEDULED));

            repository.save(build("Midnight in Colombo", "Thriller", "English", "Hall 1",
                    today.plusDays(1), LocalTime.of(21, 0), 140, 1300.0, ShowStatus.SCHEDULED));

            repository.save(build("The Last Reel", "Documentary", "Tamil", "Hall 3",
                    today.minusDays(1), LocalTime.of(16, 0), 95, 800.0, ShowStatus.COMPLETED));
        };
    }

    private Showtime build(String title, String genre, String language, String hall,
                            LocalDate date, LocalTime time, int duration, double price, ShowStatus status) {
        Showtime s = new Showtime();
        s.setMovieTitle(title);
        s.setGenre(genre);
        s.setLanguage(language);
        s.setHallName(hall);
        s.setShowDate(date);
        s.setShowTime(time);
        s.setDurationMinutes(duration);
        s.setTicketPrice(price);
        s.setStatus(status);
        return s;
    }
}
