package com.example.casemanagement.config;

import com.example.casemanagement.exception.ForbiddenException;
import com.example.casemanagement.exception.InvalidTransitionException;
import com.example.casemanagement.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler för hela applikationen.
 *
 * Syftet med denna klass är att centralisera hantering av alla undantag
 * och säkerställa att klienten alltid får konsekventa och tydliga HTTP-svar.
 *
 * Designen separerar:
 * - Domänspecifika undantag (egna exceptions)
 * - Valideringsfel (DTO)
 * - Säkerhetsrelaterade undantag (Spring Security)
 * - Generella systemfel (fallback)
 *
 * Detta förbättrar:
 * - läsbarhet
 * - underhållbarhet
 * - konsekvent API-design
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Hanterar valideringsfel från DTO-objekt (t.ex. @Valid).
     *
     * Returnerar:
     * - 400 Bad Request
     * - lista av fältfel (field -> message)
     *
     * Detta gör det möjligt för frontend att visa specifika felmeddelanden per inputfält.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Validation failed");
        response.put("errors", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Hanterar fall där en resurs inte hittas i databasen.
     *
     * Mappas till HTTP 404 för att följa REST-principer.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Hanterar affärslogiska regler där användaren saknar rättigheter.
     *
     * OBS: Detta gäller endast egna kastade undantag i service-lagret,
     * inte Spring Securitys access-kontroller.
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, Object>> handleForbidden(ForbiddenException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    /**
     * Hanterar ogiltiga tillståndsövergångar i systemets state machine.
     *
     * Exempel: försök att ändra status på ett ärende på ett otillåtet sätt.
     *
     * Returnerar 400 eftersom felet beror på en ogiltig begäran.
     */
    @ExceptionHandler(InvalidTransitionException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidTransition(InvalidTransitionException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Hanterar autentiseringsfel (fel användarnamn/lösenord).
     *
     * Returnerar 401 Unauthorized enligt HTTP-standard.
     */
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(Exception ex) {
        return buildResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    /**
     * Hanterar auktoriseringsfel från Spring Security.
     *
     * Detta inkluderar:
     * - @PreAuthorize
     * - rollbaserade restriktioner i säkerhetslagret
     *
     * Viktigt: Dessa undantag skiljer sig från egna ForbiddenException
     * och måste hanteras separat för att returnera korrekt HTTP 403.
     */
    @ExceptionHandler({
            org.springframework.security.access.AccessDeniedException.class,
            org.springframework.security.authorization.AuthorizationDeniedException.class
    })
    public ResponseEntity<Map<String, Object>> handleAccessDenied(Exception ex) {
        return buildResponse("Access denied", HttpStatus.FORBIDDEN);
    }

    /**
     * Fallback för alla oväntade fel.
     *
     * Säkerställer att interna fel inte exponerar känslig information,
     * samtidigt som klienten får ett standardiserat svar.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return buildResponse("Något gick fel", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Gemensam metod för att bygga en konsekvent API-respons.
     *
     * Detta minskar duplicerad kod och säkerställer att alla svar
     * följer samma struktur.
     */
    private ResponseEntity<Map<String, Object>> buildResponse(
            String message,
            HttpStatus status
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("message", message);

        return new ResponseEntity<>(response, status);
    }
}