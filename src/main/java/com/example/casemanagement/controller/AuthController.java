package com.example.casemanagement.controller;

import com.example.casemanagement.dto.LoginRequest;
import com.example.casemanagement.model.User;
import com.example.casemanagement.repository.UserRepository;
import com.example.casemanagement.service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    // constructor injection
    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public User register(@RequestBody LoginRequest request) {
        return authService.register(
                request.getName(),
                request.getEmail(),
                request.getPassword()
        );
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = authService.login(request.getEmail(), request.getPassword());

        return Map.of(
                "token", token,
                "role", user.getRole().name(),
                "email", user.getEmail(),
                "name", user.getName()
        );
    }
}