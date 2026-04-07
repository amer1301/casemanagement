package com.example.casemanagement.service;

import com.example.casemanagement.dto.CaseDTO;
import com.example.casemanagement.exception.ResourceNotFoundException;
import com.example.casemanagement.model.Case;
import com.example.casemanagement.model.CaseStatus;
import com.example.casemanagement.model.User;
import com.example.casemanagement.model.Role;
import com.example.casemanagement.dto.CreateCaseDTO;
import com.example.casemanagement.dto.UpdateCaseDTO;
import com.example.casemanagement.repository.CaseRepository;
import com.example.casemanagement.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CaseService {
    private final CaseRepository repo;
    private final UserRepository userRepository;
    private final CaseLogService caseLogService;

    private User getCurrentUser() {
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

    public List<CaseDTO> getAll() {
        return repo.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
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
                "Case created"
        );

        return mapToDTO(saved);
    }

    public CaseDTO getCaseById(Long id) {
        Case c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case med id " + id + " hittades inte"));

        return mapToDTO(c);
    }

    public void deleteCase(Long id) {
        Case c = repo.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        caseLogService.logAction(
                c,
                getCurrentUser(),
                "Case deleted"
        );
        repo.delete(c);
    }

    public Case updateStatus(Long id, CaseStatus newStatus) {

        Case c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        User currentUser = getCurrentUser();

        // Endast ADMIN får ändra
        if (currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only Admin can update status");
        }

        // Endast från SUBMITTED
        if (c.getStatus() != CaseStatus.SUBMITTED) {
            throw new IllegalStateException("Invalid status transition");
        }

        // ADMIN får inte godkänna sitt eget case
        if (c.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Cannot approve your own case");
        }

        c.setStatus(newStatus);
        Case updated = repo.save(c);

        // Loggning
        caseLogService.logAction(
                updated,
                currentUser,
                "Status changed to " + newStatus + " by " + currentUser.getEmail()
        );

        return updated;
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
                "Case updated"
        );
        return mapToDTO(saved);
    }

    private CaseDTO mapToDTO(Case c) {
        return new CaseDTO(
                c.getId(),
                c.getTitle(),
                c.getDescription(),
                c.getStatus().name(),
                c.getCreatedAt(),
                c.getUser().getEmail()
        );
    }
}
