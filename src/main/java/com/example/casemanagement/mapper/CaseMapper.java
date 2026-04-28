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

/**
 * Mapper för transformation mellan domänmodeller (entities)
 * och DTO:er relaterade till ärenden.
 *
 * Syfte:
 * - Separera datamodell från API-representation
 * - Samla all mappningslogik på ett ställe
 * - Minska duplicering av transformationskod i service-lagret
 *
 * Design:
 * - Används av service-lagret för att konvertera mellan lager
 * - Hanterar både inkommande (DTO → entity) och utgående (entity → DTO)
 */
@Component
public class CaseMapper {

    /**
     * Skapar en loggpost för ett ärende.
     *
     * Används för att registrera händelser i systemet (audit logging).
     */
    public CaseLog toCaseLog(Case c, User user, String action) {
        CaseLog log = new CaseLog();
        log.setCaseEntity(c);
        log.setUser(user);
        log.setAction(action);
        log.setTimestamp(LocalDateTime.now());
        return log;
    }

    /**
     * Omvandlar CaseLog-entitet till DTO.
     *
     * Används för att exponera loggdata till klienten.
     */
    public CaseLogDTO toCaseLogDTO(CaseLog log) {
        return new CaseLogDTO(
                log.getId(),
                log.getAction(),
                log.getTimestamp(),
                log.getCaseEntity().getId(),
                log.getUser() != null ? log.getUser().getEmail() : null
        );
    }

    /**
     * Skapar en ny anteckning kopplad till ett ärende.
     */
    public CaseNote toCaseNote(String text, Case c, User user) {
        return new CaseNote(text, c, user);
    }

    /**
     * Omvandlar CaseNote-entitet till DTO.
     *
     * Anpassar data för visning i frontend (t.ex. namn istället för ID).
     */
    public CaseNoteDTO toCaseNoteDTO(CaseNote n) {
        return new CaseNoteDTO(
                n.getId(),
                n.getText(),
                n.getCreatedBy().getName(),
                n.getCreatedAt()
        );
    }

    /**
     * Omvandlar Case-entitet till DTO.
     *
     * Innehåller både grunddata och UI-anpassad information,
     * exempelvis användarnamn istället för interna identifierare.
     */
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

    /**
     * Omvandlar CreateCaseDTO till Case-entitet.
     *
     * Initialiserar:
     * - status (SUBMITTED)
     * - prioritet (beräknad i service-lagret)
     * - skapandedatum
     *
     * Notering:
     * - Affärslogik (t.ex. prioritering) hanteras inte här
     */
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