package com.example.casemanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entitet som representerar en begäran om rolländring.
 *
 * Används när en användare ansöker om att få en annan roll,
 * exempelvis att bli administratör.
 *
 * Syfte:
 * - Separera rollhantering från direkt användaruppdatering
 * - Möjliggöra en godkännandeprocess (workflow)
 *
 * Design:
 * - Varje begäran är kopplad till en användare
 * - Status används för att representera processen (t.ex. PENDING, APPROVED, REJECTED)
 * - Soft delete används för att behålla historik utan att visa borttagna poster
 *
 * Process:
 * - Skapas med status PENDING
 * - Hanteras av MANAGER som kan godkänna eller avslå
 */
@Entity
public class RoleRequest {

    /** Primärnyckel */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Användaren som begär rolländring */
    @ManyToOne
    private User user;

    /** Status för begäran (workflow) */
    @Enumerated(EnumType.STRING)
    private RoleRequestStatus status;

    /**
     * Soft delete-flagga
     *
     * Notering:
     * - Behåller historik i databasen
     * - Filtreras bort i applikationslogik/UI
     */
    @Column(name = "is_deleted")
    private boolean deleted = false;

    /** Tidpunkt då begäran skapades */
    private LocalDateTime createdAt;

    public RoleRequest() {}

    /**
     * Konstruktor som initierar en ny rollbegäran.
     *
     * Sätter:
     * - användare
     * - status till PENDING
     * - skapandetid
     */
    public RoleRequest(User user) {
        this.user = user;
        this.status = RoleRequestStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // getters & setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RoleRequestStatus getStatus() {
        return status;
    }

    public void setStatus(RoleRequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}