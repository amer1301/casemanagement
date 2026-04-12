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
import com.example.casemanagement.exception.ForbiddenException;
import com.example.casemanagement.domain.CaseStatusTransition;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;

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
        System.out.println("CURRENT USER ROLE: " + currentUser.getRole());

        // Endast ADMIN får ändra
        if (currentUser.getRole() != Role.ADMIN) {
            throw new ForbiddenException("Only admin can update status");
        }

        // Hämta ärende
        Case c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        // Får inte ändra sitt eget ärende
        if (c.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Cannot approve your own case");
        }

        // Central validering
        CaseStatusTransition.validate(c.getStatus(), newStatus);

        // Uppdatera
        c.setStatus(newStatus);

        Case saved = repo.save(c);

        // Loggning
        caseLogService.logAction(
                saved,
                currentUser,
                "STATUS_CHANGED " + newStatus + " by " + currentUser.getEmail()
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
        return new CaseDTO(
                c.getId(),
                c.getTitle(),
                c.getDescription(),
                c.getStatus().name(),
                c.getCreatedAt(),
                c.getUser().getEmail()
        );
    }

    public List<CaseDTO> getByStatus(CaseStatus status) {
        return repo.findByStatus(status)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

}
