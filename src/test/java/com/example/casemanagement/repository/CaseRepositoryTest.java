package com.example.casemanagement.repository;

import com.example.casemanagement.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CaseRepositoryTest {

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private UserRepository userRepository;

    // ===================== HELPERS =====================

    private Case createValidCase(User user) {
        Case c = new Case();
        c.setTitle("Test Case");
        c.setDescription("Test Description");
        c.setCategory(CaseCategory.STUDY);
        c.setApplicantName("Test Person");
        c.setPersonalNumber("19900101-1234");
        c.setPriority(3);
        c.setStatus(CaseStatus.SUBMITTED);
        c.setUser(user);
        return c;
    }

    private User createUser(String email) {
        User user = new User(
                "Test User",
                email,
                "password",
                Role.USER
        );
        return userRepository.saveAndFlush(user);
    }

    // ===================== BASIC SAVE =====================

    @Test
    @DisplayName("Should save and retrieve case")
    void shouldSaveAndFindCase() {
        User user = createUser("basic-" + System.nanoTime() + "@test.com");

        Case c = createValidCase(user);
        caseRepository.saveAndFlush(c);

        List<Case> all = caseRepository.findAll();

        assertEquals(1, all.size());
        assertEquals("Test Case", all.get(0).getTitle());
    }

    // ===================== FIND BY USER =====================

    @Test
    void shouldFindByUser() {
        User user = createUser("user-" + System.nanoTime() + "@test.com");

        caseRepository.saveAndFlush(createValidCase(user));

        List<Case> result = caseRepository.findByUser(user);

        assertEquals(1, result.size());
    }

    // ===================== FIND BY STATUS =====================

    @Test
    void shouldFindByStatus() {
        User user = createUser("status-" + System.nanoTime() + "@test.com");

        Case c = createValidCase(user);
        c.setStatus(CaseStatus.APPROVED);

        caseRepository.saveAndFlush(c);

        List<Case> result = caseRepository.findByStatus(CaseStatus.APPROVED);

        assertEquals(1, result.size());
    }

    @Test
    void shouldReturnEmptyWhenStatusNotFound() {
        List<Case> result = caseRepository.findByStatus(CaseStatus.REJECTED);
        assertTrue(result.isEmpty());
    }

    // ===================== ASSIGNED =====================

    @Test
    void shouldFindUnassignedCases() {
        User user = createUser("null-" + System.nanoTime() + "@test.com");

        caseRepository.saveAndFlush(createValidCase(user));

        List<Case> result = caseRepository.findByAssignedToIsNull();

        assertEquals(1, result.size());
    }

    @Test
    void shouldFindByAssignedTo() {
        User creator = createUser("creator-" + System.nanoTime() + "@test.com");
        User admin = createUser("admin-" + System.nanoTime() + "@test.com");

        Case c = createValidCase(creator);
        c.setAssignedTo(admin);

        caseRepository.saveAndFlush(c);

        List<Case> result = caseRepository.findByAssignedTo(admin);

        assertEquals(1, result.size());
    }

    // ===================== DATE RANGE =====================

    @Test
    void shouldFindByCreatedAtBetween() {
        User user = createUser("date-" + System.nanoTime() + "@test.com");

        Case c = createValidCase(user);
        caseRepository.saveAndFlush(c);

        LocalDateTime now = LocalDateTime.now();

        List<Case> result = caseRepository.findByCreatedAtBetween(
                now.minusSeconds(5),
                now.plusSeconds(5)
        );

        assertEquals(1, result.size());
    }

    // ===================== COUNT =====================

    @Test
    void shouldCountAssignedAndUnassigned() {
        User user = createUser("count-" + System.nanoTime() + "@test.com");
        User admin = createUser("admin2-" + System.nanoTime() + "@test.com");

        Case c1 = createValidCase(user);
        Case c2 = createValidCase(user);
        c2.setAssignedTo(admin);

        caseRepository.saveAllAndFlush(List.of(c1, c2));

        assertEquals(1, caseRepository.countByAssignedToIsNull());
        assertEquals(1, caseRepository.countByAssignedToIsNotNull());
    }

    @Test
    void shouldCountByAssignedToAndStatus() {
        User user = createUser("count2-" + System.nanoTime() + "@test.com");
        User admin = createUser("admin3-" + System.nanoTime() + "@test.com");

        Case c = createValidCase(user);
        c.setAssignedTo(admin);
        c.setStatus(CaseStatus.APPROVED);

        caseRepository.saveAndFlush(c);

        assertEquals(1, caseRepository.countByAssignedTo(admin));
        assertEquals(1, caseRepository.countByAssignedToAndStatus(admin, CaseStatus.APPROVED));
        assertEquals(0, caseRepository.countByAssignedToAndStatusNot(admin, CaseStatus.APPROVED));
    }

    // ===================== CUSTOM QUERY =====================

    @Test
    void shouldFetchCaseWithUser() {
        User user = createUser("fetch-" + System.nanoTime() + "@test.com");

        Case c = createValidCase(user);
        caseRepository.saveAndFlush(c);

        Case result = caseRepository.findByIdWithUser(c.getId()).orElseThrow();

        assertNotNull(result.getUser());

        // säkerställer att relationen är fetchad (inte lazy problem)
        assertDoesNotThrow(() -> result.getUser().getEmail());
    }
}