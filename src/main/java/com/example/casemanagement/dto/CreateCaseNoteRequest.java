package com.example.casemanagement.dto;

import jakarta.validation.constraints.NotNull;

public class CreateCaseNoteRequest {

    private Long caseId;

    @NotNull
    private String text;

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}