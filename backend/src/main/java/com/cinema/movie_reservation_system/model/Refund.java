package com.cinema.movie_reservation_system.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Model representing a Ticket Cancellation / Refund Request.
 * Part of Refunds & Cancellations Management (IT25102432).
 */
public class Refund {

    private Long id;
    private Long bookingId;
    private Double refundAmountLkr;
    private String reason;
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime requestedAt;

    // Helper fields from booking join
    private String customerName;
    private String customerEmail;
    private String movieTitle;
    private String hallName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate showDate;

    @JsonFormat(pattern = "HH:mm[:ss]")
    private LocalTime startTime;

    private Double bookingTotalLkr;

    public Refund() {
        this.requestedAt = LocalDateTime.now();
    }

    public Refund(Long bookingId, Double refundAmountLkr, String reason, String status) {
        this.bookingId = bookingId;
        this.refundAmountLkr = refundAmountLkr;
        this.reason = reason;
        this.status = (status != null && !status.isBlank()) ? status.toUpperCase() : "PENDING";
        this.requestedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Double getRefundAmountLkr() {
        return refundAmountLkr;
    }

    public void setRefundAmountLkr(Double refundAmountLkr) {
        this.refundAmountLkr = refundAmountLkr;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = (status != null && !status.isBlank()) ? status.toUpperCase() : "PENDING";
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
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

    public Double getBookingTotalLkr() {
        return bookingTotalLkr;
    }

    public void setBookingTotalLkr(Double bookingTotalLkr) {
        this.bookingTotalLkr = bookingTotalLkr;
    }

    @Override
    public String toString() {
        return "Refund{" +
                "id=" + id +
                ", bookingId=" + bookingId +
                ", refundAmountLkr=" + refundAmountLkr +
                ", reason='" + reason + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
