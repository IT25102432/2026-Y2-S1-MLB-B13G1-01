//change if needed
package com.cinema.movie_reservation_system.model;

import jakarta.persistence.*;

@Entity
@Table(name = "box_office_staff")
@PrimaryKeyJoinColumn(name = "user_id")
public class BoxOfficeStaff extends Staff {
}