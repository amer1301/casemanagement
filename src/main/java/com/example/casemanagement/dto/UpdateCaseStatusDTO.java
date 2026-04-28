package com.example.casemanagement.dto;

import com.example.casemanagement.model.CaseStatus;
import jakarta.validation.constraints.NotNull;

/**
 * DTO för uppdatering av ärendestatus.
 *
 * Representerar en statusförändring som initieras från klienten.
 *
 * Validering:
 * - status är obligatorisk
 *
 * Design:
 * - Statusändringar hanteras separat från övriga uppdateringar
 *   för att möjliggöra strikt kontroll av tillåtna övergångar
 * - reason används exempelvis vid avslag eller andra beslut
 *
 * Syfte:
 * - Säkerställa att statusändringar sker via ett kontrollerat flöde
 * - Möjliggöra koppling till affärsregler och state machine i service-lagret
 *
 * Notering:
 * - Själva valideringen av tillåtna statusövergångar sker inte här,
 *   utan i domänlogik (CaseStatusTransition) och service-lagret
 */
public class UpdateCaseStatusDTO {

    @NotNull(message = "Status måste anges")
    private CaseStatus status;

    private String reason;

    public CaseStatus getStatus() {
        return status;
    }

    public void setStatus(CaseStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}