package com.example.casemanagement.service;

import com.example.casemanagement.exception.ForbiddenException;
import com.example.casemanagement.model.Case;
import com.example.casemanagement.model.CaseStatus;
import com.example.casemanagement.model.Role;
import com.example.casemanagement.model.User;

import com.example.casemanagement.repository.CaseRepository;
import com.example.casemanagement.repository.UserRepository;

import com.example.casemanagement.domain.CaseStatusTransition;

import org.springframework.stereotype.Service;

/**
 * Service som hanterar statusändringar för ärenden.
 *
 * Ansvar:
 * - Validera att statusändringar är tillåtna
 * - Uppdatera status
 * - Logga ändringar
 * - Skicka notifieringar
 *
 * Design:
 * - Innehåller affärsregler (inte controller/repository)
 * - Använder en separat state machine (CaseStatusTransition)
 * - Bryter ut valideringar i egna metoder för tydlighet
 */
@Service
public class CaseStatusService {

    private final CaseRepository repo;
    private final UserRepository userRepository;
    private final CaseLogService caseLogService;
    private final NotificationService notificationService;

    public CaseStatusService(CaseRepository repo,
                             UserRepository userRepository,
                             CaseLogService caseLogService,
                             NotificationService notificationService) {
        this.repo = repo;
        this.userRepository = userRepository;
        this.caseLogService = caseLogService;
        this.notificationService = notificationService;
    }

    /**
     * Uppdaterar status för ett ärende.
     *
     * Flöde:
     * 1. Validera behörighet och regler
     * 2. Kontrollera att statusövergång är tillåten
     * 3. Uppdatera status och ev. avslagsorsak
     * 4. Spara
     * 5. Logga
     * 6. Skicka notifiering
     */
    public Case updateStatus(Case c, CaseStatus newStatus, String reason, User currentUser) {

        // Säkerhets- och affärsregler
        validateAdmin(currentUser);
        validateNotOwnCase(c, currentUser);
        validateCaseIsOpen(c);
        validateAssigned(c);

        // Validera state transition (state machine)
        CaseStatusTransition.validate(c.getStatus(), newStatus);

        // Sätt avslagsorsak vid avslag
        if (newStatus == CaseStatus.REJECTED) {
            c.setRejectionReason(reason);
        }

        // Uppdatera status
        c.setStatus(newStatus);

        Case saved = repo.save(c);

        // Logga händelsen
        caseLogService.logAction(
                saved,
                currentUser,
                "STATUS_CHANGED_" + newStatus
        );

        // Skicka notifiering
        sendStatusNotification(saved, newStatus, currentUser);

        return saved;
    }

    /**
     * Validerar att användaren är admin.
     */
    private void validateAdmin(User user) {
        if (user.getRole() != Role.ADMIN) {
            throw new ForbiddenException("Only admin can update status");
        }
    }

    /**
     * Förhindrar att en användare uppdaterar sitt eget ärende.
     */
    private void validateNotOwnCase(Case c, User user) {
        if (c.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Cannot update your own case");
        }
    }

    /**
     * Säkerställer att ärendet fortfarande är öppet (SUBMITTED).
     */
    private void validateCaseIsOpen(Case c) {
        if (c.getStatus() != CaseStatus.SUBMITTED) {
            throw new ForbiddenException("Ärendet är redan avslutat");
        }
    }

    /**
     * Säkerställer att ärendet är tilldelat innan statusändring.
     */
    private void validateAssigned(Case c) {
        if (c.getAssignedTo() == null) {
            throw new ForbiddenException("Du måste ta ärendet innan du ändrar status");
        }
    }

    /**
     * Skickar notifiering till användaren när status ändras.
     *
     * Notifiering skickas inte om användaren uppdaterar sitt eget ärende.
     */
    private void sendStatusNotification(Case saved, CaseStatus newStatus, User currentUser) {

        // Skicka inte notifiering till sig själv
        if (currentUser.getId().equals(saved.getUser().getId())) return;

        String message;

        // Anpassa meddelande beroende på status
        if (newStatus == CaseStatus.APPROVED) {
            message = "Ditt ärende '" + saved.getTitle() + "' har blivit godkänt";
        } else if (newStatus == CaseStatus.REJECTED) {
            message = "Ditt ärende '" + saved.getTitle() + "' har blivit avslaget";
        } else {
            message = "Ditt ärende '" + saved.getTitle() + "' uppdaterades till " + newStatus;
        }

        // Skapa notifiering
        notificationService.createNotification(
                saved.getUser(),
                message,
                saved.getId()
        );
    }
}