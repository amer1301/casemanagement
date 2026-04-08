package com.example.casemanagement.dto;

import com.example.casemanagement.model.CaseStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateCaseStatusDTO {

    @NotNull(message = "Status måste anges")
    private CaseStatus status;

    public CaseStatus getStatus() {
        return status;
    }

    public void setStatus(CaseStatus status) {
        this.status = status;
    }
}
