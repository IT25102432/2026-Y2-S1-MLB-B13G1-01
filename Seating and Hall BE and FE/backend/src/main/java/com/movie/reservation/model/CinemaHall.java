package com.movie.reservation.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(
    name = "cinema_halls",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "hall_name")
    }
)
public class CinemaHall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Hall name is required")
    @Column(name = "hall_name", nullable = false, unique = true)
    private String hallName;

    @NotNull(message = "Rows are required")
    @Min(value = 1, message = "Rows must be at least 1")
    @Max(value = 26, message = "Rows cannot exceed 26")
    private Integer rowsCount;

    @NotNull(message = "Columns are required")
    @Min(value = 1, message = "Columns must be at least 1")
    @Max(value = 30, message = "Columns cannot exceed 30")
    private Integer columnsCount;

    private Integer capacity;

    @NotNull(message = "Hall status is required")
    private Boolean active = true;

    public CinemaHall() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }

    public Integer getRowsCount() {
        return rowsCount;
    }

    public void setRowsCount(Integer rowsCount) {
        this.rowsCount = rowsCount;
    }

    public Integer getColumnsCount() {
        return columnsCount;
    }

    public void setColumnsCount(Integer columnsCount) {
        this.columnsCount = columnsCount;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}