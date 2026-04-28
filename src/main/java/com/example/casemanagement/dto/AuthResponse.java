package com.example.casemanagement.dto;

/**
 * DTO för autentiseringssvar vid inloggning.
 *
 * Innehåller:
 * - JWT-token för autentiserade anrop
 * - grundläggande användarinformation (email, namn, roll)
 *
 * Syfte:
 * - Ge klienten all nödvändig information direkt efter inloggning
 * - Undvika extra anrop för att hämta användardata
 *
 * Design:
 * - Endast dataöverföring, ingen affärslogik
 * - Immutable via konstruktor (inga setters)
 */
public class AuthResponse {

    private String token;
    private String email;
    private String name;
    private String role;

    public AuthResponse(String token, String email, String name, String role) {
        this.token = token;
        this.email = email;
        this.name = name;
        this.role = role;
    }

    public String getToken() { return token; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getRole() { return role; }
}