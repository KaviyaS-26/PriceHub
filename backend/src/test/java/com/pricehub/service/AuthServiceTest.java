package com.pricehub.service;

import com.pricehub.dto.AuthRequest;
import com.pricehub.dto.AuthResponse;
import com.pricehub.dto.RegisterRequest;
import com.pricehub.entity.User;
import com.pricehub.repository.UserPreferenceRepository;
import com.pricehub.repository.UserRepository;
import com.pricehub.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private UserRepository userRepository;
    private UserPreferenceRepository userPreferenceRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider tokenProvider;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userPreferenceRepository = Mockito.mock(UserPreferenceRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        tokenProvider = Mockito.mock(JwtTokenProvider.class);

        authService = new AuthService(
                userRepository,
                userPreferenceRepository,
                passwordEncoder,
                tokenProvider
        );
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Alice Test");
        request.setEmail("alice@test.com");
        request.setPassword("Secret123");
        request.setConfirmPassword("Secret123");
        request.setPhoneNumber("+91 9999999999");

        when(userRepository.existsByEmail("alice@test.com")).thenReturn(false);
        when(passwordEncoder.encode("Secret123")).thenReturn("hashedPassword123");
        
        User savedUser = new User("Alice Test", "alice@test.com", "hashedPassword123", "+91 9999999999");
        savedUser.setId(10L);
        savedUser.setRole("ROLE_USER");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(tokenProvider.generateToken("alice@test.com", 10L, "ROLE_USER")).thenReturn("mock-jwt-token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals("alice@test.com", response.getEmail());
        assertEquals("Alice Test", response.getFullName());
    }

    @Test
    void testRegisterDuplicateEmailThrowsException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@test.com");
        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
    }

    @Test
    void testLoginSuccess() {
        AuthRequest request = new AuthRequest("user@test.com", "Password123");
        User user = new User("User Name", "user@test.com", "hashed123", "+91 1234567890");
        user.setId(5L);
        user.setRole("ROLE_USER");
        user.setAccountStatus("ACTIVE");

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123", "hashed123")).thenReturn(true);
        when(tokenProvider.generateToken("user@test.com", 5L, "ROLE_USER")).thenReturn("login-jwt-token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("login-jwt-token", response.getToken());
        assertEquals(5L, response.getId());
    }
}

