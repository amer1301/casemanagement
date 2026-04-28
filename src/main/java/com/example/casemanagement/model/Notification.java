package com.example.casemanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entitet som representerar en notifikation i systemet.
 *
 * Används för att informera användare om viktiga händelser,
 * exempelvis tilldelning av ärenden eller statusändringar.
 *
 * Syfte:
 * - Möjliggöra användarspecifik feedback från systemet
 * - Förbättra användarupplevelsen genom notifieringar
 *
 * Design:
 * - Varje notifikation är kopplad till en specifik användare
 * - Referens till ärende lagras via caseId (löst kopplad relation)
 * - Stöd för soft delete (tas bort från UI men finns kvar i databasen)
 */
@Entity
public class Notification {

    /** Primärnyckel */
    @Id
    @GeneratedValue
    private Long id;

    /** Meddelandet som visas för användaren */
    private String message;

    /** Anger om notifikationen har lästs */
    private boolean isRead = false;

    /** Tidpunkt då notifikationen skapades */
    private LocalDateTime createdAt;

    /**
     * Referens till relaterat ärende
     *
     * Notera:
     * - Lagras som ID istället för relation för att minska koppling
     */
    private Long caseId;

    /**
     * Användaren som notifikationen tillhör
     */
    @ManyToOne
    private User user;

    /**
     * Soft delete-flagga
     *
     * Notering:
     * - Notifikationen tas inte bort från databasen
     * - Filtreras bort i applikationslogik/UI
     */
    @Column(name = "is_deleted")
    private boolean deleted = false;

    // Get & Set

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}