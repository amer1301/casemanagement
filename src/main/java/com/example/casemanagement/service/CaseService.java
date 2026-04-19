package com.example.casemanagement.service;

import com.example.casemanagement.dto.AdminStatsDTO;
import com.example.casemanagement.dto.CaseDTO;
import com.example.casemanagement.dto.CreateCaseDTO;
import com.example.casemanagement.dto.UpdateCaseDTO;
import com.example.casemanagement.exception.ForbiddenException;
import com.example.casemanagement.exception.ResourceNotFoundException;
import com.example.casemanagement.model.Case;
import com.example.casemanagement.model.CaseStatus;
import com.example.casemanagement.model.Role;
import com.example.casemanagement.model.User;
import com.example.casemanagement.model.CaseCategory;
import com.example.casemanagement.repository.CaseRepository;
import com.example.casemanagement.repository.UserRepository;
import com.example.casemanagement.domain.CaseStatusTransition;

import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CaseService {

    private final CaseRepository repo;
    private final UserRepository userRepository;
    private final CaseLogService caseLogService;
    private final NotificationService notificationService;

    public CaseService(CaseRepository repo,
                       UserRepository userRepository,
                       CaseLogService caseLogService,
                       NotificationService notificationService) {
        this.repo = repo;
        this.userRepository = userRepository;
        this.caseLogService = caseLogService;
        this.notificationService = notificationService;
    }

    protected User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Page<CaseDTO> getAll(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());

        return repo.findAll(pageable).map(this::mapToDTO);
    }

    public List<CaseDTO> getMyCases() {
        User user = getCurrentUser();
        return repo.findByUser(user)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public CaseDTO create(CreateCaseDTO dto) {

        User user = getCurrentUser();

        Case c = new Case();

        c.setTitle(dto.getTitle());
        c.setDescription(dto.getDescription());

        c.setCategory(dto.getCategory());
        c.setPersonalNumber(dto.getPersonalNumber());
        c.setApplicantName(dto.getApplicantName());

        c.setPriority(determinePriority(dto));

        c.setUser(user);
        c.setStatus(CaseStatus.SUBMITTED);
        c.setCreatedAt(LocalDateTime.now());

        Case saved = repo.save(c);

        caseLogService.logAction(saved, user, "CASE_CREATED");

        List<User> admins = userRepository.findByRole(Role.ADMIN);

        for (User admin : admins) {
            notificationService.createNotification(
                    admin,
                    "Nytt ärende har skapats: " + saved.getTitle(),
                    saved.getId()
            );
        }

        return mapToDTO(saved);
    }

    public CaseDTO getCaseById(Long id) {
        Case c = repo.findByIdWithUser(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        return mapToDTO(c);
    }

    public void deleteCase(Long id) {
        Case c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        caseLogService.logAction(c, getCurrentUser(), "CASE_DELETED");
        repo.delete(c);
    }

    public CaseDTO updateStatus(Long id, CaseStatus newStatus, String reason) {

        User currentUser = getCurrentUser();

        if (currentUser.getRole() != Role.ADMIN) {
            throw new ForbiddenException("Only admin can update status");
        }

        Case c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        // Admin får inte ändra sitt eget ärende
        if (c.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Cannot update your own case");
        }

        // Tillåt bara ändring om ärendet är öppet
        if (c.getStatus() != CaseStatus.SUBMITTED) {
            throw new ForbiddenException("Ärendet är redan avslutat");
        }

        // Säkerställ att admin har tagit ärendet
        if (c.getAssignedTo() == null) {
            throw new ForbiddenException("Du måste ta ärendet innan du ändrar status");
        }

        CaseStatusTransition.validate(c.getStatus(), newStatus);

        // Spara anledning vid avslag
        if (newStatus == CaseStatus.REJECTED) {
            c.setRejectionReason(reason);
        }

        // Hantera ROLE_REQUEST
        if ("ROLE_REQUEST".equals(c.getType())) {

            User targetUser = c.getUser();

            if (newStatus == CaseStatus.APPROVED) {
                targetUser.setRole(Role.ADMIN);
                userRepository.save(targetUser);

                caseLogService.logAction(
                        c,
                        currentUser,
                        "USER_PROMOTED_TO_ADMIN"
                );
            }

            if (newStatus == CaseStatus.REJECTED) {
                caseLogService.logAction(
                        c,
                        currentUser,
                        "ADMIN_REQUEST_REJECTED"
                );
            }
        }

        // Uppdatera status
        c.setStatus(newStatus);

        Case saved = repo.save(c);

        // Logg
        caseLogService.logAction(
                saved,
                currentUser,
                "STATUS_CHANGED_" + newStatus
        );

        // NOTIS TILL USER
        if (!currentUser.getId().equals(saved.getUser().getId())) {

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

        return mapToDTO(saved);
    }

    public CaseDTO update(Long id, UpdateCaseDTO dto) {
        Case existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());

        Case saved = repo.save(existing);

        caseLogService.logAction(saved, getCurrentUser(), "CASE_UPDATED");

        return mapToDTO(saved);
    }

    private CaseDTO mapToDTO(Case c) {

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
        dto.setType(c.getType());

        return dto;
    }

    public List<CaseDTO> getByStatus(CaseStatus status) {
        User user = getCurrentUser();

        if (user.getRole() != Role.MANAGER) {
            return List.of();
        }

        return repo.findByStatus(status)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public CaseDTO requestAdmin() {
        User user = getCurrentUser();

        boolean alreadyExists = repo.existsByUserAndTypeAndStatus(
                user,
                "ROLE_REQUEST",
                CaseStatus.SUBMITTED
        );

        if (alreadyExists) {
            throw new RuntimeException("Du har redan en aktiv admin-begäran");
        }

        Case c = new Case();
        c.setTitle("Administratörsbegäran");
        c.setDescription("Användare " + user.getEmail() + " vill få administrationsbehörighet");
        c.setStatus(CaseStatus.SUBMITTED);
        c.setUser(user);
        c.setType("ROLE_REQUEST");
        c.setCreatedAt(LocalDateTime.now());

        Case saved = repo.save(c);

        caseLogService.logAction(saved, user, "ROLE_REQUEST_CREATED");

        return mapToDTO(saved);
    }

    public CaseDTO approveRole(Long id) {
        ensureManager();

        Case c = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        if (!"ROLE_REQUEST".equals(c.getType())) {
            throw new RuntimeException("Wrong type");
        }

        User user = c.getUser();
        user.setRole(Role.ADMIN);
        userRepository.save(user);

        User manager = getCurrentUser();
        c.setAssignedTo(manager);

        c.setStatus(CaseStatus.APPROVED);

        return mapToDTO(repo.save(c));
    }

    public CaseDTO rejectRole(Long id) {
        ensureManager(); // stoppar admin direkt

        Case c = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        // säkerställ att det är rätt typ av ärende
        if (!"ROLE_REQUEST".equals(c.getType())) {
            throw new RuntimeException("Wrong type");
        }

        // förhindra dubbelhantering
        if (c.getStatus() != CaseStatus.SUBMITTED) {
            throw new RuntimeException("Case already handled");
        }

        // sätt status
        c.setStatus(CaseStatus.REJECTED);

        return mapToDTO(repo.save(c));
    }

    public Map<String, Object> getDashboardStats() {

        User currentUser = getCurrentUser();

        long total;
        long pending;
        long handled;

        if (currentUser.getRole() == Role.MANAGER) {
            total = repo.count();
            pending = repo.countByAssignedToIsNull();
            handled = repo.countByAssignedToIsNotNull();
        } else {
            total = repo.countByAssignedTo(currentUser);
            pending = repo.countByAssignedToAndStatus(currentUser, CaseStatus.SUBMITTED);
            handled = repo.countByAssignedToAndStatusNot(currentUser, CaseStatus.SUBMITTED);
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("pending", pending);
        stats.put("handled", handled);

        return stats;
    }

    public CaseDTO assignToCurrentUser(Long caseId) {

        Case c = repo.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        User currentUser = getCurrentUser();

        User previousAdmin = c.getAssignedTo();

        // Om samma admin redan har ärendet → gör inget
        if (previousAdmin != null && previousAdmin.getId().equals(currentUser.getId())) {
            return mapToDTO(c);
        }

        // Sätt ny handläggare
        c.setAssignedTo(currentUser);

        Case saved = repo.save(c);

        // LOGG
        if (previousAdmin == null) {
            caseLogService.logAction(
                    saved,
                    currentUser,
                    "CASE_ASSIGNED_TO" + currentUser.getEmail()
            );
        } else {
            caseLogService.logAction(
                    saved,
                    currentUser,
                    "CASE_REASSIGNED"
            );
        }

        // NOTIS TILL USER
        if (!currentUser.getId().equals(saved.getUser().getId())) {
            notificationService.createNotification(
                    saved.getUser(),
                    "Ditt ärende '" + saved.getTitle() + "' har blivit tilldelat en handläggare",
                    saved.getId()
            );
        }

        // NOTIS TILL TIDIGARE ADMIN (vid takeover)
        if (previousAdmin != null &&
                !previousAdmin.getId().equals(currentUser.getId())) {

            notificationService.createNotification(
                    previousAdmin,
                    "Ett ärende du hanterade ('" + saved.getTitle() + "') har tagits över av en annan admin",
                    saved.getId()
            );
        }

        return mapToDTO(saved);
    }

    public List<AdminStatsDTO> getAdminStats() {

        List<User> admins = userRepository.findByRole(Role.ADMIN);

        return admins.stream().map(admin -> {

            long total = repo.countByAssignedTo(admin);
            long handled = repo.countByAssignedToAndStatusNot(admin, CaseStatus.SUBMITTED);
            long pending = repo.countByAssignedToAndStatus(admin, CaseStatus.SUBMITTED);

            return new AdminStatsDTO(
                    admin.getName(),
                    total,
                    handled,
                    pending
            );

        }).toList();
    }

    public List<CaseDTO> getUnassignedCases() {
        return repo.findByAssignedToIsNull()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<CaseDTO> getMyAssignedCases() {
        User currentUser = getCurrentUser();

        return repo.findByAssignedTo(currentUser)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public CaseDTO appealCase(Long id, String reason) {

        User currentUser = getCurrentUser();

        Case c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        // Endast ägaren får överklaga
        if (!c.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Du kan bara överklaga dina egna ärenden");
        }

        // Måste vara avslaget
        if (c.getStatus() != CaseStatus.REJECTED) {
            throw new ForbiddenException("Endast avslagna ärenden kan överklagas");
        }

        // Redan överklagat
        if (c.isAppealed()) {
            throw new ForbiddenException("Ärendet är redan överklagat");
        }

        // ✔ Spara överklagan
        c.setAppealed(true);
        c.setAppealReason(reason);
        c.setStatus(CaseStatus.SUBMITTED); // öppna igen

        Case saved = repo.save(c);

// LOGG
        caseLogService.logAction(
                saved,
                currentUser,
                "CASE_APPEALED"
        );

// NOTIS TILL ALLA ADMINS
        List<User> admins = userRepository.findByRole(Role.ADMIN);

        for (User admin : admins) {
            notificationService.createNotification(
                    admin,
                    "Ett ärende har överklagats: " + saved.getTitle(),
                    saved.getId()
            );
        }

        return mapToDTO(saved);
    }

    private void ensureManager() {
        User user = getCurrentUser();
        if (user.getRole() != Role.MANAGER) {
            throw new RuntimeException("Forbidden");
        }
    }

    private int determinePriority(CreateCaseDTO dto) {

        String description = dto.getDescription().toLowerCase();
        CaseCategory category = dto.getCategory();

        if (description.contains("akut")) {
            return 5;
        }

        switch (category) {
            case HOUSING:
            case SICKNESS_BENEFIT:
                return 4;

            case STUDY:
            case PARENTAL_LEAVE:
                return 3;

            case UNEMPLOYMENT_SUPPORT:
                return 5;

            default:
                return 2;
        }
    }

    public void updatePriority(Long id, Integer priority) {
        Case c = repo.findById(id).orElseThrow();
        c.setPriority(priority);
    }
}