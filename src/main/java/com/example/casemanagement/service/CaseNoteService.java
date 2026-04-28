package com.example.casemanagement.service;

import com.example.casemanagement.dto.CaseNoteDTO;
import com.example.casemanagement.dto.CreateCaseNoteRequest;
import com.example.casemanagement.mapper.CaseMapper;
import com.example.casemanagement.model.Case;
import com.example.casemanagement.model.CaseNote;
import com.example.casemanagement.model.User;
import com.example.casemanagement.repository.CaseNoteRepository;
import com.example.casemanagement.repository.CaseRepository;
import com.example.casemanagement.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service för hantering av anteckningar kopplade till ärenden.
 *
 * Ansvar:
 * - Skapa nya anteckningar
 * - Hämta anteckningar för ett ärende
 *
 * Design:
 * - Hämtar aktuell användare via SecurityContext
 * - Använder mapper för konvertering mellan DTO och Entity
 * - Håller affärslogik separerad från controller och repository
 */
@Service
public class CaseNoteService {

    private final CaseNoteRepository noteRepo;
    private final CaseRepository caseRepo;
    private final UserRepository userRepo;
    private final CaseMapper mapper;

    public CaseNoteService(CaseNoteRepository noteRepo,
                           CaseRepository caseRepo,
                           UserRepository userRepo,
                           CaseMapper mapper) {
        this.noteRepo = noteRepo;
        this.caseRepo = caseRepo;
        this.userRepo = userRepo;
        this.mapper = mapper;
    }

    /**
     * Hämtar den aktuellt inloggade användaren.
     *
     * Använder Spring Securitys SecurityContext för att identifiera
     * vem som utför operationen.
     */
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepo.findByEmail(email)
                .orElseThrow();
    }

    /**
     * Skapar en ny anteckning kopplad till ett ärende.
     *
     * Flöde:
     * 1. Hämta ärendet
     * 2. Hämta aktuell användare
     * 3. Skapa anteckning via mapper
     * 4. Spara och returnera DTO
     */
    public CaseNoteDTO createNote(CreateCaseNoteRequest request) {

        // Hämta ärende
        Case c = caseRepo.findById(request.getCaseId())
                .orElseThrow();

        // Hämta aktuell användare
        User user = getCurrentUser();

        // Skapa anteckning
        CaseNote note = mapper.toCaseNote(
                request.getText(),
                c,
                user
        );

        // Spara och returnera DTO
        return mapper.toCaseNoteDTO(
                noteRepo.save(note)
        );
    }

    /**
     * Hämtar alla anteckningar för ett specifikt ärende.
     *
     * Flöde:
     * 1. Hämta från repository
     * 2. Mappa till DTO
     */
    public List<CaseNoteDTO> getNotes(Long caseId) {
        return noteRepo.findByCaseEntityId(caseId)
                .stream()
                .map(mapper::toCaseNoteDTO)
                .toList();
    }
}