package com.example.casemanagement.integration;

import com.example.casemanagement.model.*;
import com.example.casemanagement.repository.CaseNoteRepository;
import com.example.casemanagement.repository.CaseRepository;
import com.example.casemanagement.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CaseFlowIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private CaseNoteRepository caseNoteRepository;

    @Test
    void shouldCreateCaseAndAddNoteFlow() {

        User user = new User(
                "Test User",
                "test@test.com",
                "password",
                Role.USER
        );

        user = userRepository.save(user);

        assertNotNull(user.getId());

        Case c = new Case();
        c.setTitle("Test Case");
        c.setDescription("Test Description");
        c.setUser(user);
        c.setStatus(CaseStatus.SUBMITTED);

        c.setCategory(CaseCategory.STUDY);
        c.setApplicantName("Test Person");
        c.setPersonalNumber("19900101-1234");

        c.setPriority(3);

        c = caseRepository.save(c);

        assertNotNull(c.getId());

        CaseNote note = new CaseNote();
        note.setText("Integration note");
        note.setCaseEntity(c);
        note.setCreatedBy(user);

        caseNoteRepository.save(note);

        List<CaseNote> notes =
                caseNoteRepository.findByCaseEntityId(c.getId());

        assertEquals(1, notes.size());

        CaseNote savedNote = notes.get(0);

        assertEquals("Integration note", savedNote.getText());
        assertEquals(user.getId(), savedNote.getCreatedBy().getId());
        assertEquals(c.getId(), savedNote.getCaseEntity().getId());
    }
}