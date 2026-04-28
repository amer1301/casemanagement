package com.example.casemanagement.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO för registrering av nya användare.
 *
 * Representerar indata från klienten vid skapande av ett nytt konto.
 *
 * Validering:
 * - name, email och password är obligatoriska och får inte vara null
 *
 * Syfte:
 * - Separera API-indata från intern användarmodell
 * - Säkerställa att nödvändig information finns innan användaren skapas
 *
 * Säkerhet:
 * - Lösenord hanteras endast temporärt och krypteras i service-lagret
 * - Ingen känslig information lagras direkt i DTO
 *
 * Design:
 * - Används tillsammans med @Valid i controller
 * - Enkel struktur utan affärslogik
 */
public class RegisterRequest {

    @NotNull
    private String name;

    @NotNull
    private String email;

    @NotNull
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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