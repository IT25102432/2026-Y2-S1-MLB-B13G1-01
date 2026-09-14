//change if needed
package com.cinema.movie_reservation_system.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "staff")
@PrimaryKeyJoinColumn(name = "user_id")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Staff extends AppUser {

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
}