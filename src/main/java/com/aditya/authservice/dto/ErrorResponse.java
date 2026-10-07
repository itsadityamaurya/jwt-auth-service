package com.aditya.authservice.dto;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(Instant timestamp, int status, String message, Map<String, String> errors) {
    public static ErrorResponse of(int status, String message) {
        return new ErrorResponse(Instant.now(), status, message, null);
    }
}
