package com.cinema.movie_reservation_system.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Model representing a Seat Reservation / Ticket Booking.
 * Part of Seat Reservations & Ticket Booking (IT25100266).
 */
public class Booking {

    private Long id;
    private Long showtimeId;
    private String customerName;
    private String customerEmail;
    private Double totalAmountLkr;
    private String status = "CONFIRMED"; // CONFIRMED or CANCELLED

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    // Associated Seats
    private List<Long> seatIds = new ArrayList<>();
    private List<String> seatLabels = new ArrayList<>();

    // Details from Showtime/Movie/Hall joins
    private String movieTitle;
    private String hallName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate showDate;

    @JsonFormat(pattern = "HH:mm[:ss]")
    private LocalTime startTime;

    private String voucherCode;
    private Double discountLkr;

    public Booking() {
        this.createdAt = LocalDateTime.now();
    }

    public Booking(Long showtimeId, String customerName, String customerEmail, Double totalAmountLkr, String status) {
        this.showtimeId = showtimeId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.totalAmountLkr = totalAmountLkr;
        this.status = (status != null && !status.isBlank()) ? status.toUpperCase() : "CONFIRMED";
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(Long showtimeId) {
        this.showtimeId = showtimeId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public Double getTotalAmountLkr() {
        return totalAmountLkr;
    }

    public void setTotalAmountLkr(Double totalAmountLkr) {
        this.totalAmountLkr = totalAmountLkr;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = (status != null && !status.isBlank()) ? status.toUpperCase() : "CONFIRMED";
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Long> getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(List<Long> seatIds) {
        this.seatIds = (seatIds != null) ? seatIds : new ArrayList<>();
    }

    public List<String> getSeatLabels() {
        return seatLabels;
    }

    public void setSeatLabels(List<String> seatLabels) {
        this.seatLabels = (seatLabels != null) ? seatLabels : new ArrayList<>();
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

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public Double getDiscountLkr() {
        return discountLkr;
    }

    public void setDiscountLkr(Double discountLkr) {
        this.discountLkr = discountLkr;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", showtimeId=" + showtimeId +
                ", customerName='" + customerName + '\'' +
                ", totalAmountLkr=" + totalAmountLkr +
                ", status='" + status + '\'' +
                '}';
    }
}
