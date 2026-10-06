package com.cinema.movie_reservation_system.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Model representing a Scheduled Showtime.
 * Part of Showtime Scheduling & Theater Assignment (IT25103071).
 */
public class Showtime {

    private Long id;
    private Long movieId;
    private Long hallId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate showDate;

    @JsonFormat(pattern = "HH:mm[:ss]")
    private LocalTime startTime;

    // Helper fields populated from joins
    private String movieTitle;
    private String hallName;
    private Integer durationMins;
    private Double basePrice;

    public Showtime() {
    }

    public Showtime(Long movieId, Long hallId, LocalDate showDate, LocalTime startTime) {
        this.movieId = movieId;
        this.hallId = hallId;
        this.showDate = showDate;
        this.startTime = startTime;
    }

    public Showtime(Long id, Long movieId, Long hallId, LocalDate showDate, LocalTime startTime) {
        this.id = id;
        this.movieId = movieId;
        this.hallId = hallId;
        this.showDate = showDate;
        this.startTime = startTime;
    }

    public Showtime(Long id, Long movieId, Long hallId, LocalDate showDate, LocalTime startTime,
                    String movieTitle, String hallName, Integer durationMins, Double basePrice) {
        this.id = id;
        this.movieId = movieId;
        this.hallId = hallId;
        this.showDate = showDate;
        this.startTime = startTime;
        this.movieTitle = movieTitle;
        this.hallName = hallName;
        this.durationMins = durationMins;
        this.basePrice = basePrice;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public Long getHallId() {
        return hallId;
    }

    public void setHallId(Long hallId) {
        this.hallId = hallId;
    }

    public LocalDate getShowDate() {
        return showDate;
    }

    public void setShowDate(LocalDate showDate) {
        this.showDate = showDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }

    public Integer getDurationMins() {
        return durationMins;
    }

    public void setDurationMins(Integer durationMins) {
        this.durationMins = durationMins;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public LocalTime getEndTime() {
        if (startTime != null && durationMins != null) {
            return startTime.plusMinutes(durationMins);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Showtime{" +
                "id=" + id +
                ", movieId=" + movieId +
                ", hallId=" + hallId +
                ", showDate=" + showDate +
                ", startTime=" + startTime +
                ", movieTitle='" + movieTitle + '\'' +
                ", hallName='" + hallName + '\'' +
                '}';
    }
}
