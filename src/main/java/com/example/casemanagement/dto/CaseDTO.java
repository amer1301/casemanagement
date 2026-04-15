package com.example.casemanagement.dto;

import java.time.LocalDateTime;

public class CaseDTO {

    private Long id;
    private String title;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private String userEmail;
    private String assignedToName;
    private String rejectionReason;

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

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getUserEmail() { return userEmail; }
    public String getAssignedToName() { return assignedToName; }
    public String getRejectionReason() { return rejectionReason; }

    public void setAssignedToName(String assignedToName) {
        this.assignedToName = assignedToName;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}