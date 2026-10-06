package com.cinema.movie_reservation_system.model;

/**
 * Model representing a booked seat mapping for a reservation.
 * Part of Seat Reservations & Ticket Booking (IT25100266).
 */
public class BookingSeat {

    private Long id;
    private Long bookingId;
    private Long seatId;

    public BookingSeat() {
    }

    public BookingSeat(Long bookingId, Long seatId) {
        this.bookingId = bookingId;
        this.seatId = seatId;
    }

    public BookingSeat(Long id, Long bookingId, Long seatId) {
        this.id = id;
        this.bookingId = bookingId;
        this.seatId = seatId;
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

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }
}
