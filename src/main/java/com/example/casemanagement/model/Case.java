package com.example.casemanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cases")
public class Case {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Titeln får inte vara tom")
    @Size(min = 3, max = 100, message = "Titeln måste vara mellan 3 och 100 tecken")
    private String title;

    @NotBlank(message = "Beskrivning får inte vara tom")
    @Size(max = 500, message = "Beskrivning får max vara 500 tecken")
    private String description;

    @Enumerated(EnumType.STRING)
    private CaseStatus status;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(
            mappedBy = "caseEntity",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CaseLog> logs = new java.util.ArrayList<>();

    // Tom konstruktor
    public Case() {
    }

    // Konstruktor
    public Case(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // Get & Set
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CaseStatus getStatus() {
        return status;
    }

    public void setStatus(CaseStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<CaseLog> getLogs() {
        return logs;
    }

    public void setLogs(List<CaseLog> logs) {
        this.logs = logs;
    }
    public void addLog(CaseLog log) {
        logs.add(log);
        log.setCaseEntity(this);
    }
}
