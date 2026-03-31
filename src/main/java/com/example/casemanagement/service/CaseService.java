package com.example.casemanagement.service;

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
}
