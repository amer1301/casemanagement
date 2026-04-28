package com.example.casemanagement.controller;

import com.example.casemanagement.dto.ApiResponse;
import com.example.casemanagement.dto.AuthResponse;
import com.example.casemanagement.dto.LoginRequest;
import com.example.casemanagement.dto.RegisterRequest;
import com.example.casemanagement.service.AuthService;
import org.springframework.web.bind.annotation.*;

/**
 * Controller för autentisering och användarregistrering.
 *
 * Denna klass ansvarar enbart för att:
 * - ta emot HTTP-anrop
 * - validera input (via DTO)
 * - vidarebefordra till service-lagret
 * - returnera standardiserade API-svar
 *
 * All affärslogik hanteras i AuthService, vilket säkerställer
 * en tydlig separation mellan presentation (controller) och logik (service).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registrerar en ny användare.
     *
     * Tar emot användardata via DTO och delegerar skapandet till service-lagret.
     * Ingen affärslogik implementeras här.
     */
    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody RegisterRequest request) {

        authService.register(request);

        // Returnerar ett standardiserat svar utan att exponera intern logik
        return new ApiResponse<>("User registered successfully");
    }

    /**
     * Autentiserar en användare och returnerar en JWT-token.
     *
     * Flöde:
     * 1. Validera credentials via AuthService
     * 2. Generera JWT-token
     * 3. Bygg ett svar med token och användarinformation
     */
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody LoginRequest request) {

        String token = authService.login(request);

        AuthResponse response = authService.buildAuthResponse(
                request.getEmail(),
                token
        );

        // Token returneras till klienten för vidare autentiserade anrop
        return new ApiResponse<>(response);
    }
}