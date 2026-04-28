package com.example.casemanagement.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO för inloggningsförfrågan.
 *
 * Representerar användarens indata vid autentisering.
 *
 * Validering:
 * - email och password får inte vara null
 *
 * Syfte:
 * - Separera API-indata från intern användarmodell
 * - Säkerställa att nödvändig information finns innan autentisering sker
 *
 * Säkerhet:
 * - Känslig data (lösenord) hanteras endast temporärt och lagras inte i DTO
 * - Validering sker innan vidare bearbetning i service-lagret
 */
public class LoginRequest {

    @NotNull
    private String email;

    @NotNull
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}