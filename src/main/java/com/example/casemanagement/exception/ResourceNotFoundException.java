package com.example.casemanagement.exception;

/**
 * Custom exception för resurser som inte hittas (HTTP 404).
 *
 * Används när en efterfrågad resurs inte finns i systemet,
 * exempelvis ett ärende eller en användare.
 *
 * Exempel:
 * - Hämtning av ett ärende med ett ID som inte existerar
 * - Uppdatering eller borttagning av en resurs som saknas
 *
 * Design:
 * - Ärver från RuntimeException för att kunna hanteras centralt
 *   via GlobalExceptionHandler
 * - Mappas till HTTP 404 (Not Found) i exception handler
 *
 * Syfte:
 * - Ge tydlig feedback till klienten när en resurs saknas
 * - Separera denna typ av fel från behörighets- och affärsregel-fel
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}