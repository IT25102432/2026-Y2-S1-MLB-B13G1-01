package com.cinema.movie_reservation_system.model;

/**
 * Model representing a Cinema Hall in the reservation system.
 * Part of Seating Layout and Hall Allocation (IT25102154).
 */
public class Hall {

    private Long id;
    private String name;
    private int totalRows;
    private int seatsPerRow;
    private String hallType;
    private Double basePrice = 1200.0;

    // Default constructor
    public Hall() {
    }

    // Parameterized constructor without ID (useful when creating new halls)
    public Hall(String name, int totalRows, int seatsPerRow, String hallType) {
        this.name = name;
        this.totalRows = totalRows;
        this.seatsPerRow = seatsPerRow;
        this.hallType = hallType;
        this.basePrice = 1200.0;
    }

    public Hall(String name, int totalRows, int seatsPerRow, String hallType, Double basePrice) {
        this.name = name;
        this.totalRows = totalRows;
        this.seatsPerRow = seatsPerRow;
        this.hallType = hallType;
        this.basePrice = (basePrice != null && basePrice > 0) ? basePrice : 1200.0;
    }

    // Full parameterized constructor
    public Hall(Long id, String name, int totalRows, int seatsPerRow, String hallType) {
        this.id = id;
        this.name = name;
        this.totalRows = totalRows;
        this.seatsPerRow = seatsPerRow;
        this.hallType = hallType;
        this.basePrice = 1200.0;
    }

    public Hall(Long id, String name, int totalRows, int seatsPerRow, String hallType, Double basePrice) {
        this.id = id;
        this.name = name;
        this.totalRows = totalRows;
        this.seatsPerRow = seatsPerRow;
        this.hallType = hallType;
        this.basePrice = (basePrice != null && basePrice > 0) ? basePrice : 1200.0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getSeatsPerRow() {
        return seatsPerRow;
    }

    public void setSeatsPerRow(int seatsPerRow) {
        this.seatsPerRow = seatsPerRow;
    }

    public String getHallType() {
        return hallType;
    }

    public void setHallType(String hallType) {
        this.hallType = hallType;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    // Helper method to compute total capacity
    public int getTotalCapacity() {
        return this.totalRows * this.seatsPerRow;
    }

    @Override
    public String toString() {
        return "Hall{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", totalRows=" + totalRows +
                ", seatsPerRow=" + seatsPerRow +
                ", hallType='" + hallType + '\'' +
                ", basePrice=" + basePrice +
                ", totalCapacity=" + getTotalCapacity() +
                '}';
    }
}
