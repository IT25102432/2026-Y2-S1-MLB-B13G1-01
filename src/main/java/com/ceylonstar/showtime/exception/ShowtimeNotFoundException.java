package com.ceylonstar.showtime.exception;

public class ShowtimeNotFoundException extends RuntimeException {
    public ShowtimeNotFoundException(Long id) {
        super("No showtime found with id " + id);
    }
}
