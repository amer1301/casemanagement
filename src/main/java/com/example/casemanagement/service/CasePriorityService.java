package com.example.casemanagement.service;

import com.example.casemanagement.dto.CreateCaseDTO;
import com.example.casemanagement.exception.ResourceNotFoundException;
import com.example.casemanagement.model.Case;
import com.example.casemanagement.model.CaseCategory;
import com.example.casemanagement.repository.CaseRepository;
import org.springframework.stereotype.Service;

@Service
public class CasePriorityService {

    private final CaseRepository caseRepository;

    public CasePriorityService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    public int determinePriority(CreateCaseDTO dto) {

        String description = dto.getDescription().toLowerCase();
        CaseCategory category = dto.getCategory();

        if (description.contains("akut")) return 5;

        return switch (category) {
            case HOUSING, SICKNESS_BENEFIT -> 4;
            case STUDY, PARENTAL_LEAVE -> 3;
            case UNEMPLOYMENT_SUPPORT -> 5;
            default -> 2;
        };
    }

    public void updatePriority(Long id, Integer newPriority) {

        Case c = caseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        c.setPriority(newPriority);

        caseRepository.save(c);
    }
}