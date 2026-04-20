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

    public Case updateStatus(Case c, CaseStatus newStatus, String reason, User currentUser) {

        validateAdmin(currentUser);
        validateNotOwnCase(c, currentUser);
        validateCaseIsOpen(c);
        validateAssigned(c);

        CaseStatusTransition.validate(c.getStatus(), newStatus);

        if (newStatus == CaseStatus.REJECTED) {
            c.setRejectionReason(reason);
        }

        handleRoleRequest(c, newStatus, currentUser);

        c.setStatus(newStatus);

        Case saved = repo.save(c);

        caseLogService.logAction(
                saved,
                currentUser,
                "STATUS_CHANGED_" + newStatus
        );

        sendStatusNotification(saved, newStatus, currentUser);

        return saved;
    }

    private void validateAdmin(User user) {
        if (user.getRole() != Role.ADMIN) {
            throw new ForbiddenException("Only admin can update status");
        }
    }

    private void validateNotOwnCase(Case c, User user) {
        if (c.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Cannot update your own case");
        }
    }

    private void validateCaseIsOpen(Case c) {
        if (c.getStatus() != CaseStatus.SUBMITTED) {
            throw new ForbiddenException("Ärendet är redan avslutat");
        }
    }

    private void validateAssigned(Case c) {
        if (c.getAssignedTo() == null) {
            throw new ForbiddenException("Du måste ta ärendet innan du ändrar status");
        }
    }

    private void handleRoleRequest(Case c, CaseStatus newStatus, User currentUser) {

        if (!"ROLE_REQUEST".equals(c.getType())) return;

        User targetUser = c.getUser();

        if (newStatus == CaseStatus.APPROVED) {
            targetUser.setRole(Role.ADMIN);
            userRepository.save(targetUser);

            caseLogService.logAction(c, currentUser, "USER_PROMOTED_TO_ADMIN");
        }

        if (newStatus == CaseStatus.REJECTED) {
            caseLogService.logAction(c, currentUser, "ADMIN_REQUEST_REJECTED");
        }
    }

    private void sendStatusNotification(Case saved, CaseStatus newStatus, User currentUser) {

        if (currentUser.getId().equals(saved.getUser().getId())) return;

        String message;

        if (newStatus == CaseStatus.APPROVED) {
            message = "Ditt ärende '" + saved.getTitle() + "' har blivit godkänt";
        } else if (newStatus == CaseStatus.REJECTED) {
            message = "Ditt ärende '" + saved.getTitle() + "' har blivit avslaget";
        } else {
            message = "Ditt ärende '" + saved.getTitle() + "' uppdaterades till " + newStatus;
        }

        notificationService.createNotification(
                saved.getUser(),
                message,
                saved.getId()
        );
    }
}