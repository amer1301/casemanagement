package com.example.casemanagement.controller;

import com.example.casemanagement.dto.ApiResponse;
import com.example.casemanagement.dto.AuthResponse;
import com.example.casemanagement.dto.LoginRequest;
import com.example.casemanagement.dto.RegisterRequest;
import com.example.casemanagement.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody RegisterRequest request) {

        authService.register(request);

        return new ApiResponse<>("User registered successfully");
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody LoginRequest request) {

        String token = authService.login(request);

        AuthResponse response = authService.buildAuthResponse(
                request.getEmail(),
                token
        );

        return new ApiResponse<>(response);
    }
}