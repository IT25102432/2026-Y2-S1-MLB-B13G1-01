package com.ceylonstar.showtime.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "showtimes")
public class Showtime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Movie title is required")
    @Size(max = 120, message = "Movie title must be under 120 characters")
    @Column(nullable = false, length = 120)
    private String movieTitle;

    @NotBlank(message = "Genre is required")
    @Size(max = 60)
    @Column(nullable = false, length = 60)
    private String genre;

    @NotBlank(message = "Language is required")
    @Size(max = 40)
    @Column(nullable = false, length = 40)
    private String language;

    @NotBlank(message = "Hall is required")
    @Column(nullable = false, length = 40)
    private String hallName;

    @NotNull(message = "Show date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate showDate;

    @NotNull(message = "Show time is required")
    @JsonFormat(pattern = "HH:mm")
    @Column(nullable = false)
    private LocalTime showTime;

    @NotNull(message = "Duration is required")
    @Min(value = 30, message = "Duration must be at least 30 minutes")
    @Max(value = 400, message = "Duration must be under 400 minutes")
    @Column(nullable = false)
    private Integer durationMinutes;

    @NotNull(message = "Ticket price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Ticket price cannot be negative")
    @Column(nullable = false)
    private Double ticketPrice;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ShowStatus status = ShowStatus.SCHEDULED;

    public Showtime() {
    }

    // ---- Getters and setters ----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }

    public LocalDate getShowDate() {
        return showDate;
    }

    public void setShowDate(LocalDate showDate) {
        this.showDate = showDate;
    }

    public LocalTime getShowTime() {
        return showTime;
    }

    public void setShowTime(LocalTime showTime) {
        this.showTime = showTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(Double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public ShowStatus getStatus() {
        return status;
    }

    public void setStatus(ShowStatus status) {
        this.status = status;
    }

    @Transient
    public LocalTime getEndTime() {
        if (showTime == null || durationMinutes == null) {
            return null;
        }
        return showTime.plusMinutes(durationMinutes);
    }
}
