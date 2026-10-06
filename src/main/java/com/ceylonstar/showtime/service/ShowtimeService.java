package com.ceylonstar.showtime.service;

import com.ceylonstar.showtime.exception.ShowtimeConflictException;
import com.ceylonstar.showtime.exception.ShowtimeNotFoundException;
import com.ceylonstar.showtime.model.ShowStatus;
import com.ceylonstar.showtime.model.Showtime;
import com.ceylonstar.showtime.repository.ShowtimeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShowtimeService {

    /** Minimum gap (minutes) required between the end of one screening and the start of the next, for cleaning/entry. */
    private static final int TURNAROUND_MINUTES = 15;

    private final ShowtimeRepository repository;

    public ShowtimeService(ShowtimeRepository repository) {
        this.repository = repository;
    }

    public List<Showtime> findAll() {
        return repository.findAllByOrderByShowDateAscShowTimeAsc();
    }

    public Showtime findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ShowtimeNotFoundException(id));
    }

    public Showtime create(Showtime showtime) {
        showtime.setId(null);
        assertNoCollision(showtime, null);
        return repository.save(showtime);
    }

    public Showtime update(Long id, Showtime incoming) {
        Showtime existing = findById(id);
        incoming.setId(existing.getId());
        assertNoCollision(incoming, existing.getId());

        existing.setMovieTitle(incoming.getMovieTitle());
        existing.setGenre(incoming.getGenre());
        existing.setLanguage(incoming.getLanguage());
        existing.setHallName(incoming.getHallName());
        existing.setShowDate(incoming.getShowDate());
        existing.setShowTime(incoming.getShowTime());
        existing.setDurationMinutes(incoming.getDurationMinutes());
        existing.setTicketPrice(incoming.getTicketPrice());
        existing.setStatus(incoming.getStatus());

        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ShowtimeNotFoundException(id);
        }
        repository.deleteById(id);
    }

    /**
     * Enforces "collision-free" scheduling: a hall cannot host two screenings
     * whose [start, end + turnaround] windows overlap on the same date.
     * Cancelled screenings are ignored so a hall slot frees up once cancelled.
     */
    private void assertNoCollision(Showtime candidate, Long excludeId) {
        // Anchor times to the actual calendar date before comparing. LocalTime alone
        // wraps at midnight (23:30 + 200min = 02:50, which looks "earlier" than
        // 23:30), which breaks the overlap math for screenings that run past
        // midnight. LocalDateTime rolls over correctly instead of wrapping.
        LocalDateTime candidateStart = LocalDateTime.of(candidate.getShowDate(), candidate.getShowTime());
        LocalDateTime candidateEnd = candidateStart.plusMinutes(candidate.getDurationMinutes() + TURNAROUND_MINUTES);

        List<Showtime> sameHallSameDay = repository.findByHallNameAndShowDate(candidate.getHallName(), candidate.getShowDate());

        for (Showtime other : sameHallSameDay) {
            if (excludeId != null && other.getId().equals(excludeId)) {
                continue;
            }
            if (other.getStatus() == ShowStatus.CANCELLED) {
                continue;
            }

            LocalDateTime otherStart = LocalDateTime.of(other.getShowDate(), other.getShowTime());
            LocalDateTime otherEnd = otherStart.plusMinutes(other.getDurationMinutes() + TURNAROUND_MINUTES);

            boolean overlaps = candidateStart.isBefore(otherEnd) && otherStart.isBefore(candidateEnd);
            if (overlaps) {
                throw new ShowtimeConflictException(
                        candidate.getHallName() + " is already booked for \"" + other.getMovieTitle() +
                                "\" from " + other.getShowTime() + " to " + otherEnd.toLocalTime() +
                                " (including a " + TURNAROUND_MINUTES + "-minute turnaround). Choose a different time or hall.");
            }
        }
    }
}
