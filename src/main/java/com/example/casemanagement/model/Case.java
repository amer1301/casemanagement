package com.example.casemanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * Entitet som representerar ett ärende i systemet.
 *
 * Denna klass är central i domänmodellen och innehåller all information
 * relaterad till ett ärendes livscykel, inklusive status, prioritet,
 * tilldelning och historik.
 *
 * Design:
 * - Mappas till databastabellen "cases"
 * - Innehåller både affärsdata och relationer till andra entiteter
 * - Används av repository-lagret för persistens
 *
 * Funktionalitet:
 * - Stödjer statusflöde (SUBMITTED → APPROVED/REJECTED)
 * - Möjliggör tilldelning av handläggare
 * - Hanterar överklaganden och beslut
 * - Innehåller loggar och anteckningar för spårbarhet
 */
@Entity
@Table(name = "cases")
public class Case {

    /** Primärnyckel */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Titel på ärendet */
    @NotBlank(message = "Titeln får inte vara tom")
    @Size(min = 3, max = 100, message = "Titeln måste vara mellan 3 och 100 tecken")
    private String title;

    /** Beskrivning av ärendet */
    @NotBlank(message = "Beskrivning får inte vara tom")
    @Size(max = 500, message = "Beskrivning får max vara 500 tecken")
    private String description;

    /** Kategori för ärendet (enum) */
    @NotNull(message = "Kategori måste anges")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseCategory category;

    /** Personnummer kopplat till ansökan */
    @NotBlank(message = "Personnummer måste anges")
    private String personalNumber;

    /** Namn på sökande */
    @NotBlank(message = "Namn måste anges")
    private String applicantName;

    /** Legacy/extra fält för admin (ej primärt använd i relation) */
    private String assignedAdmin;

    /** Prioritet används för sortering och handläggning */
    @Column(nullable = false)
    private Integer priority;

    /** Tidpunkt då beslut fattades */
    private LocalDateTime decidedAt;

    /** Aktuell status i ärendets livscykel */
    @Enumerated(EnumType.STRING)
    private CaseStatus status;

    /** Sätts automatiskt vid skapande */
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Användare som skapade ärendet
     * (kan skilja sig från "user" beroende på systemdesign)
     */
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    /** Orsak vid avslag */
    private String rejectionReason;

    /** Användaren som äger ärendet */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /** Handläggare (admin) som ansvarar för ärendet */
    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    /** Orsak vid överklagan */
    @Column(nullable = true)
    private String appealReason;

    /** Flagga som visar om ärendet har överklagats */
    @Column(nullable = false)
    private boolean appealed = false;

    /**
     * Anteckningar kopplade till ärendet
     *
     * Design:
     * - Cascade ALL: följer ärendets livscykel
     * - orphanRemoval: tas bort om de inte längre är kopplade
     */
    @OneToMany(
            mappedBy = "caseEntity",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CaseNote> notes = new ArrayList<>();

    /**
     * Loggar för spårbarhet (audit trail)
     *
     * Används för att registrera alla viktiga händelser
     * i ärendets livscykel.
     */
    @OneToMany(
            mappedBy = "caseEntity",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CaseLog> logs = new ArrayList<>();

    // Tom konstruktor (krav från JPA)
    public Case() {
    }

    // Enkel konstruktor
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

    /**
     * Hjälpmetod för att lägga till logg och samtidigt
     * sätta korrekt relation.
     */
    public void addLog(CaseLog log) {
        logs.add(log);
        log.setCaseEntity(this);
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getAppealReason() {
        return appealReason;
    }

    public void setAppealReason(String appealReason) {
        this.appealReason = appealReason;
    }

    public boolean isAppealed() {
        return appealed;
    }

    public void setAppealed(boolean appealed) {
        this.appealed = appealed;
    }

    public List<CaseNote> getNotes() {
        return notes;
    }

    public void setNotes(List<CaseNote> notes) {
        this.notes = notes;
    }

    public CaseCategory getCategory() {
        return category;
    }

    public void setCategory(CaseCategory category) {
        this.category = category;
    }

    public LocalDateTime getDecidedAt() {
        return decidedAt;
    }

    public void setDecidedAt(LocalDateTime decidedAt) {
        this.decidedAt = decidedAt;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getAssignedAdmin() {
        return assignedAdmin;
    }

    public void setAssignedAdmin(String assignedAdmin) {
        this.assignedAdmin = assignedAdmin;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }
}