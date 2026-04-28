package com.example.casemanagement.dto;

import java.time.LocalDateTime;

/**
 * DTO för överföring av ärendedata mellan backend och frontend.
 *
 * Syfte:
 * - Exponera endast relevant information från Case-entiteten
 * - Undvika att skicka hela databasmodellen direkt till klienten
 * - Möjliggöra anpassning av data för UI (t.ex. namn istället för ID)
 *
 * Innehåller både:
 * - grundläggande ärendedata (titel, status, datum)
 * - användarrelaterad information (email, assignedToName)
 * - domänspecifika fält (rejectionReason, appeal, priority)
 *
 * Design:
 * - Separat från entitet för att minska koppling mellan lager
 * - Flexibel struktur för att stödja olika vyer i frontend (dashboard, detaljer, listor)
 */
public class CaseDTO {

    private Long id;
    private String title;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private String userEmail;
    private String assignedToName;
    private String rejectionReason;
    private boolean appealed;
    private String appealReason;
    private String type;
    private String category;
    private String applicantName;
    private String personalNumber;
    private Integer priority;

    /**
     * Tom konstruktor används vid t.ex. deserialisering.
     */
    public CaseDTO() {
    }

    /**
     * Förenklad konstruktor för grundläggande data.
     *
     * Används exempelvis vid listvyer där inte all information behövs.
     */
    public CaseDTO(Long id,
                   String title,
                   String description,
                   String status,
                   LocalDateTime createdAt,
                   String userEmail) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.userEmail = userEmail;
    }

    // ====================
    // GETTERS
    // ====================

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getAssignedToName() {
        return assignedToName;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public boolean isAppealed() {
        return appealed;
    }

    public String getAppealReason() {
        return appealReason;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public Integer getPriority() {
        return priority;
    }

    // ====================
    // SETTERS
    // ====================

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setAssignedToName(String assignedToName) {
        this.assignedToName = assignedToName;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public void setAppealed(boolean appealed) {
        this.appealed = appealed;
    }

    public void setAppealReason(String appealReason) {
        this.appealReason = appealReason;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}