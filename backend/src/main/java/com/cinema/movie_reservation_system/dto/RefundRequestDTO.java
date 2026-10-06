package com.cinema.movie_reservation_system.dto;

/**
 * DTO for requesting a ticket cancellation / refund.
 * Part of Refunds & Cancellations Management (IT25102432).
 */
public class RefundRequestDTO {

    private Long bookingId;
    private String reason;
    private Double refundAmountLkr;

    public RefundRequestDTO() {
    }

    public RefundRequestDTO(Long bookingId, String reason, Double refundAmountLkr) {
        this.bookingId = bookingId;
        this.reason = reason;
        this.refundAmountLkr = refundAmountLkr;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Double getRefundAmountLkr() {
        return refundAmountLkr;
    }

    public void setRefundAmountLkr(Double refundAmountLkr) {
        this.refundAmountLkr = refundAmountLkr;
    }
}
