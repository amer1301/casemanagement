package com.example.casemanagement.controller;

import com.example.casemanagement.model.User;
import com.example.casemanagement.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public User register(@RequestParam String email, @RequestParam String password) {
        return authService.register(email, password);
    }
}
