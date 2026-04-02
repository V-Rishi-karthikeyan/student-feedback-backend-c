package com.feedbacksystem.service;

import com.feedbacksystem.dto.LoginRequest;
import com.feedbacksystem.dto.RegisterRequest;
import com.feedbacksystem.dto.AuthResponse;
import com.feedbacksystem.entity.Role;
import com.feedbacksystem.entity.User;
import com.feedbacksystem.repository.UserRepository;
import com.feedbacksystem.service.impl.AuthServiceImpl;
import com.feedbacksystem.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserDetailsService userDetailsService;
    @Mock private UserDetails userDetails;

    @InjectMocks
    private AuthServiceImpl authService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .name("Alice")
                .email("alice@test.com")
                .password("encoded_password")
                .role(Role.STUDENT)
                .build();
    }

    @Test
    void login_ValidCredentials_ReturnsAuthResponse() {
        LoginRequest request = new LoginRequest();
        request.setEmail("alice@test.com");
        request.setPassword("password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(mockUser));
        when(userDetailsService.loadUserByUsername("alice@test.com")).thenReturn(userDetails);
        when(jwtUtil.generateToken(any(UserDetails.class), anyMap())).thenReturn("mock.jwt.token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mock.jwt.token", response.getToken());
        assertEquals("alice@test.com", response.getEmail());
        assertEquals("Alice", response.getName());
        assertEquals(Role.STUDENT, response.getRole());
    }

    @Test
    void register_NewEmail_ReturnsAuthResponse() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Bob");
        request.setEmail("bob@test.com");
        request.setPassword("password123");
        request.setRole(Role.STUDENT);

        when(userRepository.existsByEmail("bob@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(
                User.builder().id(2L).name("Bob").email("bob@test.com")
                        .password("encoded_password").role(Role.STUDENT).build()
        );
        when(userDetailsService.loadUserByUsername("bob@test.com")).thenReturn(userDetails);
        when(jwtUtil.generateToken(any(UserDetails.class), anyMap())).thenReturn("mock.jwt.token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("bob@test.com", response.getEmail());
        assertEquals("Bob", response.getName());
    }

    @Test
    void register_DuplicateEmail_ThrowsException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("alice@test.com");
        request.setPassword("password123");
        request.setRole(Role.STUDENT);

        when(userRepository.existsByEmail("alice@test.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
    }
}
