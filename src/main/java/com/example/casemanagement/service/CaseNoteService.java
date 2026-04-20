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

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepo.findByEmail(email)
                .orElseThrow();
    }

    // SKAPA ANTECKNING
    public CaseNoteDTO createNote(Long caseId, String text) {

        // 1. Hämta data
        Case c = caseRepo.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        User user = getCurrentUser();

        // 2. Mappa
        CaseNote note = mapper.toCaseNote(text, c, user);

        // 3. Spara + returnera
        return mapper.toCaseNoteDTO(noteRepo.save(note));
    }

    // HÄMTA ANTECKNINGAR
    public List<CaseNoteDTO> getNotes(Long caseId) {
        return noteRepo.findByCaseEntityId(caseId)
                .stream()
                .map(mapper::toCaseNoteDTO)
                .toList();
    }
}