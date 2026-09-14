package com.cinema.movie_reservation_system.model;

/**
 * Model representing a Seat in a Cinema Hall.
 * Part of Seating Layout and Hall Allocation (IT25102154).
 */
public class Seat {

    private Long id;
    private Long hallId;
    private String seatRow;
    private int seatNumber;
    private String seatType;
    private boolean isActive;

    // Default constructor
    public Seat() {
    }

    // Parameterized constructor without ID (useful when generating new seats)
    public Seat(Long hallId, String seatRow, int seatNumber, String seatType, boolean isActive) {
        this.hallId = hallId;
        this.seatRow = seatRow;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.isActive = isActive;
    }

    // Full parameterized constructor
    public Seat(Long id, Long hallId, String seatRow, int seatNumber, String seatType, boolean isActive) {
        this.id = id;
        this.hallId = hallId;
        this.seatRow = seatRow;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.isActive = isActive;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHallId() {
        return hallId;
    }

    public void setHallId(Long hallId) {
        this.hallId = hallId;
    }

    public String getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(String seatRow) {
        this.seatRow = seatRow;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Standard getter/setter alias for Jackson JSON serialization compatibility
    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }

    // Helper method to get formatted seat code (e.g., A1, B5)
    public String getSeatLabel() {
        return this.seatRow + this.seatNumber;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "id=" + id +
                ", hallId=" + hallId +
                ", seatRow='" + seatRow + '\'' +
                ", seatNumber=" + seatNumber +
                ", seatType='" + seatType + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
