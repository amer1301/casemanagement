package com.example.casemanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Entitet för loggning av händelser kopplade till ärenden.
 *
 * Representerar en audit-logg som registrerar viktiga förändringar
 * i ett ärendes livscykel, exempelvis skapande, uppdatering och statusändringar.
 *
 * Syfte:
 * - Säkerställa spårbarhet (audit trail)
 * - Möjliggöra historik över åtgärder i systemet
 *
 * Design:
 * - Varje loggpost är kopplad till ett ärende och en användare
 * - Används tillsammans med CaseService för att registrera händelser
 */
@Entity
public class CaseLog {

    /** Primärnyckel */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Beskriver vilken typ av händelse som inträffat */
    private String action;

    /** Tidpunkt då loggposten skapades */
    private LocalDateTime timestamp;

    /**
     * Koppling till ärendet
     *
     * @JsonIgnore används för att undvika cirkulära referenser
     * vid serialisering till JSON
     */
    @ManyToOne
    @JsonIgnore
    private Case caseEntity;

    /** Användaren som utförde åtgärden */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Sätts automatiskt innan entiteten sparas i databasen.
     *
     * Använder JPA lifecycle callback för att säkerställa att
     * timestamp alltid sätts konsekvent.
     */
    @PrePersist
    public void onCreate() {
        this.timestamp = LocalDateTime.now();
    }

    // Get & Set

    public Long getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Case getCaseEntity() {
        return caseEntity;
    }

    public User getUser() {
        return user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setCaseEntity(Case caseEntity) {
        this.caseEntity = caseEntity;
    }

    public void setUser(User user) {
        this.user = user;
    }
}