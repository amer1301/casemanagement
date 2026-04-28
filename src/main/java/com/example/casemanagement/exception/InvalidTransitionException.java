package com.example.casemanagement.exception;

/**
 * Custom exception för ogiltiga statusövergångar.
 *
 * Används när en förändring av ärendestatus bryter mot
 * definierade regler i systemets state machine.
 *
 * Exempel:
 * - Försök att gå från APPROVED → SUBMITTED
 * - Försök att sätta samma status igen
 *
 * Design:
 * - Ärver från RuntimeException för central hantering
 *   via GlobalExceptionHandler
 * - Mappas till HTTP 400 (Bad Request), då felet beror på
 *   ogiltig input i förhållande till affärsregler
 *
 * Koppling:
 * - Används tillsammans med CaseStatusTransition
 *   för att validera tillåtna statusändringar
 */
public class InvalidTransitionException extends RuntimeException {

    public InvalidTransitionException(String message) {
        super(message);
    }
}