package com.example.casemanagement.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entitet som representerar en anteckning kopplad till ett ärende.
 *
 * Används för att lagra kompletterande information och kommentarer
 * som skrivs av användare eller handläggare under ärendets livscykel.
 *
 * Syfte:
 * - Möjliggöra dokumentation av manuella kommentarer
 * - Komplettera systemets automatiska loggar (CaseLog)
 *
 * Design:
 * - Varje anteckning är kopplad till ett specifikt ärende
 * - Innehåller information om vem som skapade anteckningen
 * - Tidsstämpel sätts automatiskt vid skapande
 */
@Entity
@Table(name = "case_notes")
public class CaseNote {

    /** Primärnyckel */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Själva anteckningstexten */
    @Column(nullable = false)
    private String text;

    /** Sätts automatiskt när anteckningen skapas */
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Koppling till ärendet som anteckningen tillhör
     */
    @ManyToOne
    @JoinColumn(name = "case_id")
    private Case caseEntity;

    /**
     * Användaren som skapade anteckningen
     */
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    public CaseNote() {}

    /**
     * Konstruktor för att skapa en ny anteckning
     */
    public CaseNote(String text, Case caseEntity, User createdBy) {
        this.text = text;
        this.caseEntity = caseEntity;
        this.createdBy = createdBy;
    }

    // GET & SET

    public Long getId() { return id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public Case getCaseEntity() { return caseEntity; }
    public void setCaseEntity(Case caseEntity) { this.caseEntity = caseEntity; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
}