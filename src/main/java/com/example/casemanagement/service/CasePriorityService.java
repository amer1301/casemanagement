package com.example.casemanagement.service;

import com.example.casemanagement.dto.CreateCaseDTO;
import com.example.casemanagement.exception.ResourceNotFoundException;
import com.example.casemanagement.model.Case;
import com.example.casemanagement.model.CaseCategory;
import com.example.casemanagement.repository.CaseRepository;
import org.springframework.stereotype.Service;

/**
 * Service för hantering av prioritet för ärenden.
 *
 * Ansvar:
 * - Bestämma prioritet vid skapande av ärende
 * - Uppdatera prioritet för befintliga ärenden
 *
 * Design:
 * - Affärslogik är separerad från CaseService för bättre modularitet
 * - Gör det enkelt att ändra prioriteringsregler utan att påverka andra delar
 */
@Service
public class CasePriorityService {

    private final CaseRepository caseRepository;

    public CasePriorityService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    /**
     * Bestämmer prioritet baserat på ärendets innehåll.
     *
     * Regler:
     * - Om beskrivningen innehåller "akut" → högsta prioritet
     * - Annars baseras prioritet på kategori
     *
     * @param dto data för ärendet
     * @return prioritet (1–5)
     */
    public int determinePriority(CreateCaseDTO dto) {

        String description = dto.getDescription().toLowerCase();
        CaseCategory category = dto.getCategory();

        // Regel: akut ärende får högsta prioritet
        if (description.contains("akut")) return 5;

        // Prioritet baserat på kategori
        return switch (category) {
            case HOUSING, SICKNESS_BENEFIT -> 4;
            case STUDY, PARENTAL_LEAVE -> 3;
            case UNEMPLOYMENT_SUPPORT -> 5;
            default -> 2;
        };
    }

    /**
     * Uppdaterar prioriteten för ett specifikt ärende.
     *
     * @param id ärendets ID
     * @param newPriority ny prioritet
     *
     * @throws ResourceNotFoundException om ärendet inte finns
     */
    public void updatePriority(Long id, Integer newPriority) {

        // Hämta ärende
        Case c = caseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        // Uppdatera prioritet
        c.setPriority(newPriority);

        // Spara ändringen
        caseRepository.save(c);
    }
}