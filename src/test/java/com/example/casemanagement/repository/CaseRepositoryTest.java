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

    // 🔧 Helper method (VIKTIG – undviker alla dina tidigare errors)
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
        c.setType("TEST_TYPE");
        return c;
    }

    private User createUser(String email) {
        User user = new User(
                "Test User",
                email,
                "password",
                Role.USER
        );
        return userRepository.save(user);
    }

    // ==============================
    // BASIC SAVE / FIND
    // ==============================

    @Test
    @DisplayName("Should save and retrieve case")
    void shouldSaveAndFindCase() {
        User user = createUser("basic@test.com");

        Case c = createValidCase(user);
        caseRepository.save(c);

        List<Case> all = caseRepository.findAll();

        assertEquals(1, all.size());
        assertEquals("Test Case", all.get(0).getTitle());
    }

    // ==============================
    // FIND BY USER
    // ==============================

    @Test
    void shouldFindByUser() {
        User user = createUser("user@test.com");

        caseRepository.save(createValidCase(user));

        List<Case> result = caseRepository.findByUser(user);

        assertEquals(1, result.size());
    }

    // ==============================
    // FIND BY STATUS
    // ==============================

    @Test
    void shouldFindByStatus() {
        User user = createUser("status@test.com");

        Case c = createValidCase(user);
        c.setStatus(CaseStatus.APPROVED);

        caseRepository.save(c);

        List<Case> result = caseRepository.findByStatus(CaseStatus.APPROVED);

        assertEquals(1, result.size());
    }

    // ==============================
    // ASSIGNED TO NULL
    // ==============================

    @Test
    void shouldFindUnassignedCases() {
        User user = createUser("null@test.com");

        caseRepository.save(createValidCase(user));

        List<Case> result = caseRepository.findByAssignedToIsNull();

        assertEquals(1, result.size());
    }

    // ==============================
    // ASSIGNED TO USER
    // ==============================

    @Test
    void shouldFindByAssignedTo() {
        User creator = createUser("creator@test.com");
        User admin = createUser("admin@test.com");

        Case c = createValidCase(creator);
        c.setAssignedTo(admin);

        caseRepository.save(c);

        List<Case> result = caseRepository.findByAssignedTo(admin);

        assertEquals(1, result.size());
    }

    // ==============================
    // DATE RANGE
    // ==============================

    @Test
    void shouldFindByCreatedAtBetween() {
        User user = createUser("date@test.com");

        Case c = createValidCase(user);
        caseRepository.save(c);

        LocalDateTime now = LocalDateTime.now();

        List<Case> result = caseRepository.findByCreatedAtBetween(
                now.minusDays(1),
                now.plusDays(1)
        );

        assertEquals(1, result.size());
    }

    // ==============================
    // EXISTS
    // ==============================

    @Test
    void shouldCheckExistsByUserTypeAndStatus() {
        User user = createUser("exists@test.com");

        Case c = createValidCase(user);
        c.setType("ROLE_REQUEST");
        c.setStatus(CaseStatus.SUBMITTED);

        caseRepository.save(c);

        boolean exists = caseRepository.existsByUserAndTypeAndStatus(
                user,
                "ROLE_REQUEST",
                CaseStatus.SUBMITTED
        );

        assertTrue(exists);
    }

    // ==============================
    // COUNT TESTS
    // ==============================

    @Test
    void shouldCountAssignedAndUnassigned() {
        User user = createUser("count@test.com");
        User admin = createUser("admin2@test.com");

        Case c1 = createValidCase(user); // unassigned
        Case c2 = createValidCase(user);
        c2.setAssignedTo(admin);

        caseRepository.saveAll(List.of(c1, c2));

        assertEquals(1, caseRepository.countByAssignedToIsNull());
        assertEquals(1, caseRepository.countByAssignedToIsNotNull());
    }

    @Test
    void shouldCountByAssignedToAndStatus() {
        User user = createUser("count2@test.com");
        User admin = createUser("admin3@test.com");

        Case c = createValidCase(user);
        c.setAssignedTo(admin);
        c.setStatus(CaseStatus.APPROVED);

        caseRepository.save(c);

        assertEquals(1, caseRepository.countByAssignedTo(admin));
        assertEquals(1, caseRepository.countByAssignedToAndStatus(admin, CaseStatus.APPROVED));
        assertEquals(0, caseRepository.countByAssignedToAndStatusNot(admin, CaseStatus.APPROVED));
    }

    // ==============================
    // CUSTOM QUERY
    // ==============================

    @Test
    void shouldFetchCaseWithUser() {
        User user = createUser("fetch@test.com");

        Case c = createValidCase(user);
        caseRepository.save(c);

        Case result = caseRepository.findByIdWithUser(c.getId()).orElseThrow();

        assertNotNull(result.getUser());
        assertEquals(user.getId(), result.getUser().getId());
    }
}