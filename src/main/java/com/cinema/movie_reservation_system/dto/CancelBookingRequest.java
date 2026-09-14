package com.cinema.movie_reservation_system.dto;

import java.math.BigDecimal;

public class CancelBookingRequest {

    private Long bookingId;
    private Long customerId;
    private String reason;
    private BigDecimal originalAmount;

    public CancelBookingRequest() {}

    public CancelBookingRequest(Long bookingId, Long customerId, String reason, BigDecimal originalAmount) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.reason = reason;
        this.originalAmount = originalAmount;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(BigDecimal originalAmount) {
        this.originalAmount = originalAmount;
    }
}