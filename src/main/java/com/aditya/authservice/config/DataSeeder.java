package com.aditya.authservice.config;

import com.aditya.authservice.model.Role;
import com.aditya.authservice.model.User;
import com.aditya.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed-admin.email}")
    private String adminEmail;

    @Value("${app.seed-admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail(adminEmail)) {
            userRepository.save(User.builder()
                    .name("Admin")
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .build());
        }
    }
}
