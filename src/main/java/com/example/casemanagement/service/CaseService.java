package com.example.casemanagement.service;

import com.example.casemanagement.exception.ResourceNotFoundException;
import com.example.casemanagement.model.Case;
import com.example.casemanagement.model.CaseStatus;
import com.example.casemanagement.model.User;
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

    public CaseService(CaseRepository repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
    }

    public List<Case> getAll() {
        return repo.findAll();
    }

    public List<Case> getMyCases() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return repo.findByUserEmail(email);
    }

    public Case create(Case c) {

        String email = SecurityContextHolder.getContext()
                        .getAuthentication()
                                .getName();

        User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found"));
        c.setUser(user);
        c.setStatus(CaseStatus.SUBMITTED);
        c.setCreatedAt(LocalDateTime.now());

        return repo.save(c);
    }

    public Case getCaseById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case med id " + id + " hittades inte"));
    }

    public void deleteCase(Long id) {
        repo.deleteById(id);
    }

    public Case update(Long id, Case updatedCase) {
        Case existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found with id " + id));

        existing.setTitle(updatedCase.getTitle());
        existing.setDescription(updatedCase.getDescription());

        return repo.save(existing);
    }

    public Case updateStatus(Long id, CaseStatus newStatus) {
        Case c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        if (c.getStatus() != CaseStatus.SUBMITTED) {
            throw new IllegalStateException("Invalid status transition");
        }

        c.setStatus(newStatus);

        return repo.save(c);
    }
}
