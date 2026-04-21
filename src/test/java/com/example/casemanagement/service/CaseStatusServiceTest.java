package com.example.casemanagement.service;

import com.example.casemanagement.exception.ForbiddenException;
import com.example.casemanagement.model.*;
import com.example.casemanagement.repository.CaseRepository;
import com.example.casemanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CaseStatusServiceTest {

    private final CaseRepository caseRepository = mock(CaseRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final CaseLogService caseLogService = mock(CaseLogService.class);
    private final NotificationService notificationService = mock(NotificationService.class);

    private final CaseStatusService service = new CaseStatusService(
            caseRepository,
            userRepository,
            caseLogService,
            notificationService
    );

    private User mockUser(Long id, Role role) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(id);
        when(user.getRole()).thenReturn(role);
        return user;
    }

    private Case createValidCase(User owner, User assignedTo) {
        Case c = new Case();
        c.setStatus(CaseStatus.SUBMITTED);
        c.setUser(owner);
        c.setAssignedTo(assignedTo);
        c.setTitle("Test case");
        return c;
    }

    @Test
    void shouldThrowIfNotAdmin() {

        User user = mockUser(1L, Role.USER);
        User owner = mockUser(2L, Role.USER);

        Case c = createValidCase(owner, user);

        assertThrows(ForbiddenException.class, () ->
                service.updateStatus(c, CaseStatus.APPROVED, null, user)
        );
    }

    @Test
    void shouldThrowIfUpdatingOwnCase() {

        User user = mockUser(1L, Role.ADMIN);

        Case c = createValidCase(user, user);

        assertThrows(ForbiddenException.class, () ->
                service.updateStatus(c, CaseStatus.APPROVED, null, user)
        );
    }

    @Test
    void shouldThrowIfCaseNotSubmitted() {

        User admin = mockUser(1L, Role.ADMIN);
        User owner = mockUser(2L, Role.USER);

        Case c = createValidCase(owner, admin);
        c.setStatus(CaseStatus.APPROVED);

        assertThrows(ForbiddenException.class, () ->
                service.updateStatus(c, CaseStatus.REJECTED, null, admin)
        );
    }

    @Test
    void shouldThrowIfNotAssigned() {

        User admin = mockUser(1L, Role.ADMIN);
        User owner = mockUser(2L, Role.USER);

        Case c = new Case();
        c.setStatus(CaseStatus.SUBMITTED);
        c.setUser(owner);
        c.setAssignedTo(null); // ❗ inte assigned

        assertThrows(ForbiddenException.class, () ->
                service.updateStatus(c, CaseStatus.APPROVED, null, admin)
        );
    }

    @Test
    void shouldUpdateStatusSuccessfully() {

        User admin = mockUser(1L, Role.ADMIN);
        User owner = mockUser(2L, Role.USER);

        Case c = createValidCase(owner, admin);

        when(caseRepository.save(any())).thenReturn(c);

        Case result = service.updateStatus(c, CaseStatus.APPROVED, null, admin);

        assertEquals(CaseStatus.APPROVED, result.getStatus());

        verify(caseRepository).save(c);
        verify(caseLogService).logAction(eq(c), eq(admin), contains("STATUS_CHANGED"));
        verify(notificationService).createNotification(any(), any(), any());
    }
}