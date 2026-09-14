//change if needed
package com.cinema.movie_reservation_system.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cinema_manager")
@PrimaryKeyJoinColumn(name = "user_id")
public class CinemaManager extends Staff {
}