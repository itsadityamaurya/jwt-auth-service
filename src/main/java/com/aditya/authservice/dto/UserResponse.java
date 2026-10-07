package com.aditya.authservice.dto;

import com.aditya.authservice.model.Role;
import com.aditya.authservice.model.User;

public record UserResponse(Long id, String name, String email, Role role) {
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole());
    }
}
