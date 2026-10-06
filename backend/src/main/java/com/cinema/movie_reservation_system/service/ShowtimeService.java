package com.cinema.movie_reservation_system.service;

import com.cinema.movie_reservation_system.model.Hall;
import com.cinema.movie_reservation_system.model.Movie;
import com.cinema.movie_reservation_system.model.Showtime;
import com.cinema.movie_reservation_system.repository.HallRepository;
import com.cinema.movie_reservation_system.repository.MovieRepository;
import com.cinema.movie_reservation_system.repository.ShowtimeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service handling business logic for Showtime Scheduling & Theater Assignment.
 * Implements strict overlap protection logic.
 * Part of Showtime Scheduling & Theater Assignment (IT25103071).
 */
@Service
public class ShowtimeService {

    /** Buffer time in minutes between consecutive screenings in the same hall for cleaning and turnaround. */
    private static final int TURNAROUND_BUFFER_MINUTES = 15;

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final HallRepository hallRepository;

    public ShowtimeService(ShowtimeRepository showtimeRepository,
                           MovieRepository movieRepository,
                           HallRepository hallRepository) {
        this.showtimeRepository = showtimeRepository;
        this.movieRepository = movieRepository;
        this.hallRepository = hallRepository;
    }

    public List<Showtime> getAllShowtimes() {
        return showtimeRepository.findAll();
    }

    public Showtime getShowtimeById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid showtime ID: " + id);
        }
        return showtimeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Showtime not found with ID: " + id));
    }

    @Transactional
    public Showtime createShowtime(Showtime showtime) {
        validateShowtime(showtime);
        assertNoScheduleOverlap(showtime, null);
        return showtimeRepository.save(showtime);
    }

    @Transactional
    public Showtime updateShowtime(Long id, Showtime incoming) {
        Showtime existing = getShowtimeById(id);
        incoming.setId(existing.getId());

        if (incoming.getMovieId() == null) incoming.setMovieId(existing.getMovieId());
        if (incoming.getHallId() == null) incoming.setHallId(existing.getHallId());
        if (incoming.getShowDate() == null) incoming.setShowDate(existing.getShowDate());
        if (incoming.getStartTime() == null) incoming.setStartTime(existing.getStartTime());

        validateShowtime(incoming);
        assertNoScheduleOverlap(incoming, id);
        return showtimeRepository.save(incoming);
    }

    @Transactional
    public void deleteShowtime(Long id) {
        getShowtimeById(id);
        showtimeRepository.deleteById(id);
    }

    /**
     * Overlap protection logic: prevents two movies in the same hall at overlapping times.
     * Considers movie duration plus a 15-minute turnaround buffer.
     */
    public void assertNoScheduleOverlap(Showtime candidate, Long excludeId) {
        Movie movie = movieRepository.findById(candidate.getMovieId())
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with ID: " + candidate.getMovieId()));
        Hall hall = hallRepository.findById(candidate.getHallId())
                .orElseThrow(() -> new IllegalArgumentException("Cinema hall not found with ID: " + candidate.getHallId()));

        int candidateDuration = (movie.getDurationMins() != null && movie.getDurationMins() > 0)
                ? movie.getDurationMins() : 120;

        LocalDateTime candStart = LocalDateTime.of(candidate.getShowDate(), candidate.getStartTime());
        LocalDateTime candEnd = candStart.plusMinutes(candidateDuration + TURNAROUND_BUFFER_MINUTES);

        List<Showtime> existingInHallOnDate = showtimeRepository.findByHallIdAndShowDate(candidate.getHallId(), candidate.getShowDate());

        for (Showtime other : existingInHallOnDate) {
            if (excludeId != null && other.getId().equals(excludeId)) {
                continue;
            }

            int otherDuration = (other.getDurationMins() != null && other.getDurationMins() > 0)
                    ? other.getDurationMins() : 120;

            LocalDateTime otherStart = LocalDateTime.of(other.getShowDate(), other.getStartTime());
            LocalDateTime otherEnd = otherStart.plusMinutes(otherDuration + TURNAROUND_BUFFER_MINUTES);

            boolean overlaps = candStart.isBefore(otherEnd) && otherStart.isBefore(candEnd);
            if (overlaps) {
                String existingMovie = other.getMovieTitle() != null ? other.getMovieTitle() : "Movie #" + other.getMovieId();
                throw new IllegalStateException(
                        "Showtime conflict in " + hall.getName() + ": Slot overlaps with \"" + existingMovie +
                                "\" (" + other.getStartTime() + " - " + otherEnd.toLocalTime() +
                                ", including " + TURNAROUND_BUFFER_MINUTES + "-min cleaning buffer). Please select another time or hall."
                );
            }
        }
    }

    private void validateShowtime(Showtime s) {
        if (s == null) {
            throw new IllegalArgumentException("Showtime data cannot be null.");
        }
        if (s.getMovieId() == null || s.getMovieId() <= 0) {
            throw new IllegalArgumentException("Valid movie ID is required.");
        }
        if (s.getHallId() == null || s.getHallId() <= 0) {
            throw new IllegalArgumentException("Valid cinema hall ID is required.");
        }
        if (s.getShowDate() == null) {
            throw new IllegalArgumentException("Show date is required.");
        }
        if (s.getStartTime() == null) {
            throw new IllegalArgumentException("Show start time is required.");
        }
        LocalDateTime showDateTime = LocalDateTime.of(s.getShowDate(), s.getStartTime());
        if (showDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot schedule showtimes for dates or times in the past.");
        }
    }
}
