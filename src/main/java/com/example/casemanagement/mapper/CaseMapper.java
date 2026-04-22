package com.example.casemanagement.mapper;

import com.example.casemanagement.dto.CaseDTO;
import com.example.casemanagement.dto.CreateCaseDTO;
import com.example.casemanagement.dto.CaseLogDTO;
import com.example.casemanagement.dto.CaseNoteDTO;

import com.example.casemanagement.model.Case;
import com.example.casemanagement.model.CaseLog;
import com.example.casemanagement.model.CaseNote;
import com.example.casemanagement.model.CaseStatus;
import com.example.casemanagement.model.User;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CaseMapper {

    public CaseLog toCaseLog(Case c, User user, String action) {
        CaseLog log = new CaseLog();
        log.setCaseEntity(c);
        log.setUser(user);
        log.setAction(action);
        log.setTimestamp(LocalDateTime.now());
        return log;
    }

    public CaseLogDTO toCaseLogDTO(CaseLog log) {
        return new CaseLogDTO(
                log.getId(),
                log.getAction(),
                log.getTimestamp(),
                log.getCaseEntity().getId(),
                log.getUser() != null ? log.getUser().getEmail() : null
        );
    }

    public CaseNote toCaseNote(String text, Case c, User user) {
        return new CaseNote(text, c, user);
    }

    public CaseNoteDTO toCaseNoteDTO(CaseNote n) {
        return new CaseNoteDTO(
                n.getId(),
                n.getText(),
                n.getCreatedBy().getName(), // justera om behövs
                n.getCreatedAt()            // justera om behövs
        );
    }

    public CaseDTO toCaseDTO(Case c) {
        CaseDTO dto = new CaseDTO(
                c.getId(),
                c.getTitle(),
                c.getDescription(),
                c.getStatus().name(),
                c.getCreatedAt(),
                c.getUser() != null ? c.getUser().getEmail() : "Okänd"
        );

        dto.setCategory(
                c.getCategory() != null ? c.getCategory().name() : null
        );
        dto.setApplicantName(c.getApplicantName());
        dto.setPersonalNumber(c.getPersonalNumber());
        dto.setPriority(c.getPriority());

        if (c.getAssignedTo() != null) {
            dto.setAssignedToName(c.getAssignedTo().getName());
        }

        if (c.getRejectionReason() != null) {
            dto.setRejectionReason(c.getRejectionReason());
        }

        dto.setAppealed(c.isAppealed());
        dto.setAppealReason(c.getAppealReason());

        return dto;
    }

    public Case toCase(CreateCaseDTO dto, User user, int priority) {
        Case c = new Case();

        c.setTitle(dto.getTitle());
        c.setDescription(dto.getDescription());
        c.setCategory(dto.getCategory());
        c.setPersonalNumber(dto.getPersonalNumber());
        c.setApplicantName(dto.getApplicantName());

        c.setPriority(priority);
        c.setUser(user);
        c.setStatus(CaseStatus.SUBMITTED);
        c.setCreatedAt(LocalDateTime.now());

        return c;
    }
}