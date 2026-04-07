package com.example.casemanagement.dto;
import java.time.LocalDateTime;

public class CaseLogDTO {
    public Long id;
    public String action;
    public LocalDateTime timestamp;
    public Long caseId;
    public String userEmail;

    public CaseLogDTO(Long id, String action, LocalDateTime timestamp, Long caseId, String userEmail) {
        this.id = id;
        this.action = action;
        this.timestamp = timestamp;
        this.caseId = caseId;
        this.userEmail = userEmail;
    }
}
