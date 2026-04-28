package com.example.casemanagement.dto;

import java.time.LocalDateTime;

/**
 * DTO för anteckningar kopplade till ett ärende.
 *
 * Används för att överföra information om kommentarer/anteckningar
 * från backend till frontend.
 *
 * Innehåller:
 * - själva anteckningen (text)
 * - vem som skapade den
 * - när den skapades
 *
 * Syfte:
 * - Möjliggöra visning av historik och kommunikation kring ett ärende
 * - Separera intern datamodell från API-respons
 *
 * Design:
 * - Immutable via konstruktor (inga setters)
 * - Endast dataöverföring, ingen affärslogik
 */
public class CaseNoteDTO {
    private Long id;
    private String text;
    private String createdBy;
    private LocalDateTime createdAt;

    public CaseNoteDTO(Long id, String text, String createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.text = text;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getText() { return text; }
    public String getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}