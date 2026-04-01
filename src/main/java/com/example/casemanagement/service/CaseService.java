package com.example.casemanagement.service;

import com.example.casemanagement.exception.ResourceNotFoundException;
import com.example.casemanagement.model.Case;
import com.example.casemanagement.repository.CaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CaseService {
    private final CaseRepository repo;

    public CaseService(CaseRepository repo) {
        this.repo = repo;
    }

    public List<Case> getAll() {
        return repo.findAll();
    }

    public Case create(Case c) {
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
}
