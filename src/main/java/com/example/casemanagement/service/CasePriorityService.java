package com.example.casemanagement.service;

import com.example.casemanagement.dto.CreateCaseDTO;
import com.example.casemanagement.model.CaseCategory;
import org.springframework.stereotype.Service;

@Service
public class CasePriorityService {

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
}
