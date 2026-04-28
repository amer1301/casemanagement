package com.example.casemanagement.dto;

import java.time.LocalDateTime;

/**
 * Generisk wrapper för API-svar.
 *
 * Syftet är att säkerställa en konsekvent struktur för alla responses
 * från backend till klienten.
 *
 * Innehåller:
 * - success: indikerar om anropet lyckades
 * - data: payload (om tillämpligt)
 * - message: informations- eller statusmeddelande
 * - timestamp: tidpunkt för svaret
 *
 * Design:
 * - Generisk typ (T) möjliggör återanvändning för olika datatyper
 * - Används i controllers för att standardisera API-kontraktet
 */
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String message;
    private LocalDateTime timestamp;

    /**
     * Konstruktor för svar som innehåller data.
     */
    public ApiResponse(T data) {
        this.success = true;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Konstruktor för svar som endast innehåller ett meddelande.
     */
    public ApiResponse(String message) {
        this.success = true;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public boolean isSuccess() { return success; }
    public T getData() { return data; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
}