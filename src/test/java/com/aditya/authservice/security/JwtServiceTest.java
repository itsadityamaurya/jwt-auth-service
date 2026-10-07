package com.aditya.authservice.security;

import com.aditya.authservice.model.Role;
import com.aditya.authservice.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET = "IgpX/mWJE0ze7e01P/cAMhnh8vl3JjkFV0TJdob5AsKliIilLSrfjPLzGi+DBN50";

    private final User user = User.builder().id(1L).name("Test").email("test@example.com")
            .password("x").role(Role.USER).build();

    @Test
    void generatedTokenIsValidAndContainsUsername() {
        JwtService service = new JwtService(SECRET, 60_000);
        String token = service.generateToken(user);
        assertEquals("test@example.com", service.extractUsername(token));
        assertTrue(service.isValid(token, user));
    }

    @Test
    void expiredTokenIsRejected() {
        JwtService service = new JwtService(SECRET, -1000);
        String token = service.generateToken(user);
        assertFalse(service.isValid(token, user));
    }

    @Test
    void tamperedTokenIsRejected() {
        JwtService service = new JwtService(SECRET, 60_000);
        String token = service.generateToken(user) + "x";
        assertFalse(service.isValid(token, user));
    }
}
