package com.example.casemanagement.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO för skapande av anteckningar kopplade till ett ärende.
 *
 * Representerar indata från klienten vid skapande av en ny anteckning.
 *
 * Validering:
 * - text är obligatorisk och får inte vara null
 *
 * Design:
 * - caseId sätts i controller baserat på URL-path (inte från klientens payload)
 * - Detta minskar risken för manipulation av vilken resurs anteckningen kopplas till
 *
 * Syfte:
 * - Separera API-indata från intern datamodell
 * - Säkerställa att endast giltig data når service-lagret
 */
public class CreateCaseNoteRequest {

    private Long caseId;

    @NotNull
    private String text;

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}