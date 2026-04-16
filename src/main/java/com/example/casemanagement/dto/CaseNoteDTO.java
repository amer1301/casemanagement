package com.example.casemanagement.dto;

import java.time.LocalDateTime;

public class CaseNoteDTO {
    private Long id;
    private String text;
    private String createdBy;
    private LocalDateTime createdAt;

    public CaseNoteDTO(Long id, String text, String createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.text = text;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getText() { return text; }
    public String getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
