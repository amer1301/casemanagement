package com.example.casemanagement.dto;

import java.time.LocalDateTime;

/**
 * DTO för notifieringar.
 *
 * Representerar meddelanden som skickas till användaren vid olika händelser
 * i systemet, exempelvis statusändringar eller tilldelning av ärenden.
 *
 * Innehåller:
 * - meddelandetext
 * - koppling till relaterat ärende (caseId)
 * - lässtatus (isRead)
 * - tidsstämpel för när notifieringen skapades
 *
 * Syfte:
 * - Möjliggöra visning av notifieringar i användargränssnittet
 * - Ge användaren återkoppling på systemhändelser
 *
 * Design:
 * - Separat från entitet för att kontrollera vilken information som exponeras
 * - Immutable via konstruktor (inga setters)
 */
public class NotificationDTO {

    private Long id;
    private String message;
    private Long caseId;
    private boolean isRead;
    private LocalDateTime createdAt;

    public NotificationDTO(Long id, String message, Long caseId, boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.message = message;
        this.caseId = caseId;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getMessage() { return message; }
    public Long getCaseId() { return caseId; }
    public boolean isRead() { return isRead; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}