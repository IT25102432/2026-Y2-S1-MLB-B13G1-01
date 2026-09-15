package com.cinema.movie_reservation_system.exception;

public class DuplicateMovieException extends RuntimeException {

    public DuplicateMovieException(String title) {
        super("A movie titled '" + title + "' already exists for the given release date");
    }
}
