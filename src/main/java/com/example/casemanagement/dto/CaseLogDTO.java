package com.example.casemanagement.dto;

import java.time.LocalDateTime;

/**
 * DTO för logghändelser kopplade till ett ärende.
 *
 * Används för att överföra audit-loggdata till klienten och möjliggöra
 * spårbarhet av förändringar i systemet.
 *
 * Innehåller:
 * - vilken åtgärd som utförts (action)
 * - när den utfördes (timestamp)
 * - vilket ärende det gäller (caseId)
 * - vilken användare som utförde åtgärden
 *
 * Design:
 * - Enkel och oföränderlig struktur (data carrier)
 * - Används främst i historik- och loggvyer i frontend
 */
public class CaseLogDTO {
    public Long id;
    public String action;
    public LocalDateTime timestamp;
    public Long caseId;
    public String userEmail;

    public CaseLogDTO(Long id, String action, LocalDateTime timestamp, Long caseId, String userEmail) {
        this.id = id;
        this.action = action;
        this.timestamp = timestamp;
        this.caseId = caseId;
        this.userEmail = userEmail;
    }
}