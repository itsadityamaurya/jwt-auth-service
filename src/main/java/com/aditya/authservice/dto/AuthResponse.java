package com.aditya.authservice.dto;

public record AuthResponse(String token, String tokenType, long expiresInMs) {
}
