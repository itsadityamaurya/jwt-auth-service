package com.aditya.authservice.service;

import com.aditya.authservice.dto.AuthResponse;
import com.aditya.authservice.dto.LoginRequest;
import com.aditya.authservice.dto.RegisterRequest;
import com.aditya.authservice.dto.UserResponse;
import com.aditya.authservice.exception.EmailAlreadyExistsException;
import com.aditya.authservice.model.Role;
import com.aditya.authservice.model.User;
import com.aditya.authservice.repository.UserRepository;
import com.aditya.authservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
        User user = User.builder()
                .name(request.name().trim())
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();
        return UserResponse.from(userRepository.save(user));
    }

    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().trim().toLowerCase(), request.password()));
        User user = (User) auth.getPrincipal();
        return new AuthResponse(jwtService.generateToken(user), "Bearer", jwtService.getExpirationMs());
    }
}
