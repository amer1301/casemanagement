package com.example.casemanagement.exception;

/**
 * Custom exception för förbjudna operationer (HTTP 403).
 *
 * Används när en användare försöker utföra en handling som
 * inte är tillåten enligt systemets affärsregler eller behörigheter.
 *
 * Exempel:
 * - Försök att tilldela sitt eget ärende
 * - Försök att ändra status utan rätt roll
 *
 * Design:
 * - Ärver från RuntimeException för att möjliggöra central hantering
 *   via GlobalExceptionHandler
 * - Mappas till HTTP 403 (Forbidden) i exception handler
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}