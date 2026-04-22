package com.example.casemanagement.service;

import com.example.casemanagement.dto.*;
import com.example.casemanagement.exception.ForbiddenException;
import com.example.casemanagement.exception.ResourceNotFoundException;
import com.example.casemanagement.model.*;
import com.example.casemanagement.repository.CaseRepository;
import com.example.casemanagement.repository.UserRepository;
import com.example.casemanagement.mapper.CaseMapper;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CaseService {

    private final CaseRepository repo;
    private final UserRepository userRepository;
    private final CaseLogService caseLogService;
    private final CaseMapper mapper;
    private final CaseStatusService caseStatusService;
    private final RoleRequestService roleRequestService;
    private final CasePriorityService casePriorityService;
    private final NotificationService notificationService;

    public CaseService(
            CaseRepository repo,
            UserRepository userRepository,
            CaseLogService caseLogService,
            CaseMapper mapper,
            CaseStatusService caseStatusService,
            RoleRequestService roleRequestService,
            CasePriorityService casePriorityService,
            NotificationService notificationService
    ) {
        this.repo = repo;
        this.userRepository = userRepository;
        this.caseLogService = caseLogService;
        this.mapper = mapper;
        this.caseStatusService = caseStatusService;
        this.roleRequestService = roleRequestService;
        this.casePriorityService = casePriorityService;
        this.notificationService = notificationService;
    }

    protected User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public Page<CaseDTO> getAll(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        return repo.findAll(pageable).map(mapper::toCaseDTO);
    }

    public List<CaseDTO> getMyCases() {
        User user = getCurrentUser();
        return repo.findByUser(user)
                .stream()
                .map(mapper::toCaseDTO)
                .toList();
    }

    public CaseDTO create(CreateCaseDTO dto) {

        User user = getCurrentUser();
        int priority = casePriorityService.determinePriority(dto);

        Case c = mapper.toCase(dto, user, priority);

        Case saved = repo.save(c);

        caseLogService.logAction(saved, user, "CASE_CREATED");

        return mapper.toCaseDTO(saved);
    }

    public CaseDTO getCaseById(Long id) {
        Case c = repo.findByIdWithUser(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        return mapper.toCaseDTO(c);
    }

    public void deleteCase(Long id) {
        Case c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        caseLogService.logAction(c, getCurrentUser(), "CASE_DELETED");
        repo.delete(c);
    }

    public CaseDTO update(Long id, UpdateCaseDTO dto) {
        Case existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());

        Case saved = repo.save(existing);

        caseLogService.logAction(saved, getCurrentUser(), "CASE_UPDATED");

        return mapper.toCaseDTO(saved);
    }

    public CaseDTO updateStatus(Long id, CaseStatus newStatus, String reason) {

        User currentUser = getCurrentUser();

        Case c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        Case updated = caseStatusService.updateStatus(c, newStatus, reason, currentUser);

        return mapper.toCaseDTO(updated);
    }

    public List<CaseDTO> getByStatus(CaseStatus status) {
        User user = getCurrentUser();

        if (user.getRole() != Role.MANAGER) {
            return List.of();
        }

        return repo.findByStatus(status)
                .stream()
                .map(mapper::toCaseDTO)
                .toList();
    }

    public List<CaseDTO> getUnassignedCases() {
        return repo.findByAssignedToIsNull()
                .stream()
                .map(mapper::toCaseDTO)
                .toList();
    }

    public List<CaseDTO> getMyAssignedCases() {
        User user = getCurrentUser();

        return repo.findByAssignedTo(user)
                .stream()
                .map(mapper::toCaseDTO)
                .toList();
    }

    public Map<String, Object> getDashboardStats() {

        long total = repo.count();
        long unassigned = repo.countByAssignedToIsNull();
        long assigned = repo.countByAssignedToIsNotNull();

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("unassigned", unassigned);
        stats.put("assigned", assigned);

        return stats;
    }

    public CaseDTO assignToCurrentUser(Long caseId) {

        Case c = repo.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        User user = getCurrentUser();

        if (c.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Cannot assign your own case");
        }

        if (c.getAssignedTo() != null) {
            throw new ForbiddenException("Case already assigned");
        }

        c.setAssignedTo(user);

        Case saved = repo.save(c);

        notificationService.createNotification(
                saved.getUser(),
                "Ditt ärende \"" + saved.getTitle() + "\" har tilldelats en handläggare",
                saved.getId()
        );

        return mapper.toCaseDTO(saved);
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

    public CaseDTO appealCase(Long id, String reason) {

        Case c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        User currentUser = getCurrentUser();

        c.setAppealed(true);
        c.setAppealReason(reason);
        c.setStatus(CaseStatus.SUBMITTED);

        Case saved = repo.save(c);

        caseLogService.logAction(saved, currentUser,"CASE_APPEALED");

        String message = "Ärendet '" + saved.getTitle() + "' har överklagats";

        if (saved.getAssignedTo() != null) {
            notificationService.createNotification(
                    saved.getAssignedTo(),
                    message,
                    saved.getId()
            );
        } else {
            List<User> admins = userRepository.findByRole(Role.ADMIN);

            for (User admin : admins) {
                notificationService.createNotification(
                        admin,
                        message,
                        saved.getId()
                );
            }
        }

        return mapper.toCaseDTO(saved);
    }

    public void updatePriority(Long id, Integer newPriority) {
        casePriorityService.updatePriority(id, newPriority);
    }
}