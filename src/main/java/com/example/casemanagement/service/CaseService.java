package com.example.casemanagement.service;

import com.example.casemanagement.dto.AdminStatsDTO;
import com.example.casemanagement.dto.CaseDTO;
import com.example.casemanagement.exception.ResourceNotFoundException;
import com.example.casemanagement.model.Case;
import com.example.casemanagement.model.CaseStatus;
import com.example.casemanagement.model.User;
import com.example.casemanagement.model.Role;
import com.example.casemanagement.repository.UserRepository;
import com.example.casemanagement.repository.CaseRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.casemanagement.dto.CreateCaseDTO;
import com.example.casemanagement.dto.UpdateCaseDTO;
import com.example.casemanagement.exception.ForbiddenException;
import com.example.casemanagement.domain.CaseStatusTransition;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class CaseService {
    private final CaseRepository repo;
    private final UserRepository userRepository;
    private final CaseLogService caseLogService;

    protected User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public CaseService(CaseRepository repo,
                       UserRepository userRepository,
                       CaseLogService caseLogService) {
        this.repo = repo;
        this.userRepository = userRepository;
        this.caseLogService = caseLogService;
    }

    public Page<CaseDTO> getAll(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());

        Page<CaseDTO> result = repo.findAll(pageable)
                .map(this::mapToDTO);

        System.out.println("TOTAL CASES: " + result.getTotalElements());

        return result;
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

        c.setUser(user);
        c.setStatus(CaseStatus.SUBMITTED);
        c.setCreatedAt(LocalDateTime.now());

        Case saved = repo.save(c);

        caseLogService.logAction(
                saved,
                user,
                "CASE_CREATED"
        );

        return mapToDTO(saved);
    }

    public CaseDTO getCaseById(Long id) {
        Case c = repo.findByIdWithUser(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case med id " + id + " hittades inte"));

        return mapToDTO(c);
    }

    public void deleteCase(Long id) {
        Case c = repo.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        caseLogService.logAction(
                c,
                getCurrentUser(),
                "CASE_DELETED"
        );
        repo.delete(c);
    }

    public CaseDTO updateStatus(Long id, CaseStatus newStatus) {

        User currentUser = getCurrentUser();

        if (currentUser.getRole() != Role.ADMIN) {
            throw new ForbiddenException("Only admin can update status");
        }

        Case c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        if (c.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Cannot approve your own case");
        }

        CaseStatusTransition.validate(c.getStatus(), newStatus);

        if ("ROLE_REQUEST".equals(c.getType())) {

            User targetUser = c.getUser();

            if (newStatus == CaseStatus.APPROVED) {
                targetUser.setRole(Role.ADMIN);
                userRepository.save(targetUser);

                caseLogService.logAction(
                        c,
                        currentUser,
                        "USER_PROMOTED_TO_ADMIN: " + targetUser.getEmail()
                );
            }

            if (newStatus == CaseStatus.REJECTED) {
                caseLogService.logAction(
                        c,
                        currentUser,
                        "ADMIN_REQUEST_REJECTED: " + targetUser.getEmail()
                );
            }
        }

        // Uppdatera status
        c.setStatus(newStatus);

        Case saved = repo.save(c);

        // Loggning
        caseLogService.logAction(
                saved,
                currentUser,
                "STATUS_CHANGED " + newStatus
        );

        return mapToDTO(saved);
    }

    public CaseDTO update(Long id, UpdateCaseDTO dto) {
        Case existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found with id " + id));

        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());

        Case saved = repo.save(existing);

        // Logga
        caseLogService.logAction(
                saved,
                getCurrentUser(),
                "CASE_UPDATED"
        );
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

        if (c.getAssignedTo() != null) {
            dto.setAssignedToName(c.getAssignedTo().getName());
        }

        return dto;
    }

    public List<CaseDTO> getByStatus(CaseStatus status) {
        return repo.findByStatus(status)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public CaseDTO requestAdmin() {
        User user = getCurrentUser();

        Case newCase = new Case();

        newCase.setTitle("Admin request");
        newCase.setDescription("User " + user.getEmail() + " wants admin access");

        newCase.setStatus(CaseStatus.SUBMITTED);
        newCase.setUser(user);

        newCase.setType("ROLE_REQUEST");
        newCase.setCreatedAt(LocalDateTime.now());

        Case saved = repo.save(newCase);

        caseLogService.logAction(
                saved,
                user,
                "ROLE_REQUEST_CREATED"
        );

        return mapToDTO(saved);
    }

    public CaseDTO approveRole(Long id) {

        Case c = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        if (!"ROLE_REQUEST".equals(c.getType())) {
            throw new RuntimeException("Fel typ");
        }

        User user = c.getCreatedBy();

        user.setRole(Role.ADMIN);
        userRepository.save(user);

        c.setStatus(CaseStatus.APPROVED);

        return mapToDTO(repo.save(c));
    }

    public CaseDTO rejectRole(Long id) {

        Case c = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        c.setStatus(CaseStatus.REJECTED);

        return mapToDTO(repo.save(c));
    }

    public Map<String, Object> getDashboardStats() {

        User currentUser = getCurrentUser();

        long total;
        long pending;
        long handled;

        if (currentUser.getRole() == Role.MANAGER) {
            // Manager ser ALLA
            total = repo.count();
            pending = repo.countByAssignedToIsNull();
            handled = repo.countByAssignedToIsNotNull();
        } else {
            // Admin ser sina egna
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

        User currentUser = getCurrentUser(); // du har redan denna i service

        c.setAssignedTo(currentUser);

        repo.save(c);

        return mapToDTO(c);
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
}
