package com.cinema.movie_reservation_system.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for creating a seat reservation booking.
 * Part of Seat Reservations & Ticket Booking (IT25100266).
 */
public class BookingRequest {

    private Long showtimeId;
    private String customerName;
    private String customerEmail;
    private List<Long> seatIds = new ArrayList<>();
    private String voucherCode;

    public BookingRequest() {
    }

    public BookingRequest(Long showtimeId, String customerName, String customerEmail, List<Long> seatIds, String voucherCode) {
        this.showtimeId = showtimeId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.seatIds = seatIds;
        this.voucherCode = voucherCode;
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

    public List<Long> getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(List<Long> seatIds) {
        this.seatIds = (seatIds != null) ? seatIds : new ArrayList<>();
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }
}
