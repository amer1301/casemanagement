package com.example.casemanagement.service;

import com.example.casemanagement.dto.CaseNoteDTO;
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

    public CaseNoteService(CaseNoteRepository noteRepo,
                           CaseRepository caseRepo,
                           UserRepository userRepo) {
        this.noteRepo = noteRepo;
        this.caseRepo = caseRepo;
        this.userRepo = userRepo;
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

        Case c = caseRepo.findById(caseId).orElseThrow();
        User user = getCurrentUser();

        CaseNote note = new CaseNote(text, c, user);

        return map(noteRepo.save(note));
    }

    // HÄMTA ANTECKNINGAR
    public List<CaseNoteDTO> getNotes(Long caseId) {
        return noteRepo.findByCaseEntityId(caseId)
                .stream()
                .map(this::map)
                .toList();
    }

    private CaseNoteDTO map(CaseNote n) {
        return new CaseNoteDTO(
                n.getId(),
                n.getText(),
                n.getCreatedBy().getName(),
                n.getCreatedAt()
        );
    }
}