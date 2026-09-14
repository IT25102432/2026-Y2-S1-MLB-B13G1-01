//change if needed
package com.cinema.movie_reservation_system.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "customer")
@PrimaryKeyJoinColumn(name = "user_id")
public class Customer extends AppUser {

    private String address;

    private Integer age;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
}