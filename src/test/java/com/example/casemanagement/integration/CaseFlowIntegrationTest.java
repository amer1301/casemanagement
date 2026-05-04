package com.example.casemanagement.integration;

import com.example.casemanagement.model.*;
import com.example.casemanagement.repository.CaseNoteRepository;
import com.example.casemanagement.repository.CaseRepository;
import com.example.casemanagement.repository.UserRepository;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
class CaseFlowIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private CaseNoteRepository caseNoteRepository;

    // ===================== HAPPY FLOW =====================
    @Test
    void shouldCreateCaseAndAddNoteFlow() {

        User user = new User(
                "Test User",
                "test-" + System.currentTimeMillis() + "@test.com",
                "password",
                Role.USER
        );

        user = userRepository.saveAndFlush(user);
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

        c = caseRepository.saveAndFlush(c);
        assertNotNull(c.getId());

        CaseNote note = new CaseNote();
        note.setText("Integration note");
        note.setCaseEntity(c);
        note.setCreatedBy(user);

        caseNoteRepository.saveAndFlush(note);

        List<CaseNote> notes =
                caseNoteRepository.findByCaseEntityId(c.getId());

        assertEquals(1, notes.size());

        CaseNote savedNote = notes.get(0);

        assertEquals("Integration note", savedNote.getText());
        assertEquals(user.getId(), savedNote.getCreatedBy().getId());
        assertEquals(c.getId(), savedNote.getCaseEntity().getId());
    }

    // ===================== EMPTY RESULT =====================
    @Test
    void shouldReturnEmptyWhenNoNotesExist() {

        List<CaseNote> notes =
                caseNoteRepository.findByCaseEntityId(999L);

        assertTrue(notes.isEmpty());
    }

    // ===================== MULTIPLE NOTES =====================
    @Test
    void shouldHandleMultipleNotes() {

        User user = userRepository.saveAndFlush(
                new User("User", "multi@test.com", "pass", Role.USER)
        );

        Case c = new Case();
        c.setTitle("Case");
        c.setDescription("Test description");
        c.setUser(user);
        c.setStatus(CaseStatus.SUBMITTED);
        c.setCategory(CaseCategory.STUDY);
        c.setApplicantName("Test");
        c.setPersonalNumber("19900101-1234");
        c.setPriority(1);

        c = caseRepository.saveAndFlush(c);

        CaseNote note1 = new CaseNote();
        note1.setText("Note 1");
        note1.setCaseEntity(c);
        note1.setCreatedBy(user);

        CaseNote note2 = new CaseNote();
        note2.setText("Note 2");
        note2.setCaseEntity(c);
        note2.setCreatedBy(user);

        caseNoteRepository.saveAllAndFlush(List.of(note1, note2));

        List<CaseNote> notes =
                caseNoteRepository.findByCaseEntityId(c.getId());

        assertEquals(2, notes.size());
    }
}