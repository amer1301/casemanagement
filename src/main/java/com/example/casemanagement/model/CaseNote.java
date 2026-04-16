package com.example.casemanagement.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "case_notes")
public class CaseNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // 🔗 Koppling till ärende
    @ManyToOne
    @JoinColumn(name = "case_id")
    private Case caseEntity;

    // 🔗 Vem skrev anteckningen
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    public CaseNote() {}

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

