package com.feedbacksystem.service;

import com.feedbacksystem.dto.AuthResponse;
import com.feedbacksystem.dto.LoginRequest;
import com.feedbacksystem.dto.RegisterRequest;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse register(RegisterRequest request);
}
