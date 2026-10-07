package com.aditya.authservice.controller;

import com.aditya.authservice.dto.UserResponse;
import com.aditya.authservice.model.User;
import com.aditya.authservice.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/api/users/me")
    @Operation(summary = "Get the logged-in user's profile")
    public UserResponse me(@AuthenticationPrincipal User user) {
        return UserResponse.from(user);
    }

    @GetMapping("/api/admin/users")
    @Operation(summary = "List all users (ADMIN only)")
    public List<UserResponse> allUsers() {
        return userRepository.findAll().stream().map(UserResponse::from).toList();
    }
}
