package com.ceylonstar.showtime.exception;

import java.time.Instant;
import java.util.Map;

public class ApiError {
    private final Instant timestamp = Instant.now();
    private final int status;
    private final String message;
    private final Map<String, String> fieldErrors;

    public ApiError(int status, String message) {
        this(status, message, null);
    }

    public ApiError(int status, String message, Map<String, String> fieldErrors) {
        this.status = status;
        this.message = message;
        this.fieldErrors = fieldErrors;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}
