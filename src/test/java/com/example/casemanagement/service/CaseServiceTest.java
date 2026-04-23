package com.example.casemanagement.service;

import com.example.casemanagement.exception.ForbiddenException;
import com.example.casemanagement.exception.ResourceNotFoundException;
import com.example.casemanagement.mapper.CaseMapper;
import com.example.casemanagement.model.*;
import com.example.casemanagement.repository.CaseRepository;
import com.example.casemanagement.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CaseServiceTest {

    private CaseRepository repo = mock(CaseRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);
    private CaseLogService caseLogService = mock(CaseLogService.class);
    private CaseMapper mapper = mock(CaseMapper.class);
    private CaseStatusService statusService = mock(CaseStatusService.class);
    private RoleRequestService roleRequestService = mock(RoleRequestService.class);
    private CasePriorityService priorityService = mock(CasePriorityService.class);
    private NotificationService notificationService = mock(NotificationService.class);

    private CaseService caseService;

    @BeforeEach
    void setup() {
        caseService = new CaseService(
                repo,
                userRepository,
                caseLogService,
                mapper,
                statusService,
                roleRequestService,
                priorityService,
                notificationService
        );

        SecurityContext context = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);

        when(auth.getName()).thenReturn("test@test.com");
        when(context.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(context);
    }

    // ===================== UPDATE STATUS =====================

    @Test
    void shouldDelegateStatusUpdate() {

        Case c = new Case();

        User user = mock(User.class);
        when(user.getEmail()).thenReturn("test@test.com");

        when(repo.findById(1L)).thenReturn(Optional.of(c));
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(statusService.updateStatus(any(), any(), any(), any())).thenReturn(c);
        when(mapper.toCaseDTO(any())).thenReturn(null);

        caseService.updateStatus(1L, CaseStatus.APPROVED, "ok");

        verify(statusService).updateStatus(eq(c), eq(CaseStatus.APPROVED), eq("ok"), eq(user));
    }

    @Test
    void shouldThrowWhenCaseNotFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                caseService.updateStatus(1L, CaseStatus.APPROVED, "ok")
        );
    }

    // ===================== ASSIGN =====================

    @Test
    void shouldAssignCaseToCurrentUser() {

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(2L);

        User admin = mock(User.class);
        when(admin.getId()).thenReturn(1L);
        when(admin.getEmail()).thenReturn("test@test.com");

        Case c = new Case();
        c.setUser(owner);

        when(repo.findById(10L)).thenReturn(Optional.of(c));
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(admin));
        when(repo.save(any())).thenReturn(c);
        when(mapper.toCaseDTO(any())).thenReturn(null);

        caseService.assignToCurrentUser(10L);

        assertEquals(admin, c.getAssignedTo());

        verify(notificationService).createNotification(
                eq(owner),
                contains("tilldelats"),
                any()
        );
    }

    @Test
    void shouldNotAllowAssigningOwnCase() {

        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getEmail()).thenReturn("test@test.com");

        Case c = new Case();
        c.setUser(user);

        when(repo.findById(1L)).thenReturn(Optional.of(c));
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        assertThrows(ForbiddenException.class, () ->
                caseService.assignToCurrentUser(1L)
        );
    }

    @Test
    void shouldNotAssignAlreadyAssignedCase() {

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(2L);

        User admin = mock(User.class);
        when(admin.getId()).thenReturn(1L);
        when(admin.getEmail()).thenReturn("test@test.com");

        Case c = new Case();
        c.setUser(owner);
        c.setAssignedTo(admin);

        when(repo.findById(1L)).thenReturn(Optional.of(c));
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(admin));

        assertThrows(ForbiddenException.class, () ->
                caseService.assignToCurrentUser(1L)
        );
    }

    // ===================== GET BY STATUS =====================

    @Test
    void shouldReturnEmptyIfUserNotManager() {

        User user = mock(User.class);
        when(user.getRole()).thenReturn(Role.USER);
        when(user.getEmail()).thenReturn("test@test.com");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        assertTrue(caseService.getByStatus(CaseStatus.SUBMITTED).isEmpty());
    }

    // ===================== DELETE =====================

    @Test
    void shouldDeleteCaseAndLog() {

        Case c = new Case();

        User user = mock(User.class);
        when(user.getEmail()).thenReturn("test@test.com");

        when(repo.findById(1L)).thenReturn(Optional.of(c));
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        caseService.deleteCase(1L);

        verify(repo).delete(c);
        verify(caseLogService).logAction(eq(c), eq(user), eq("CASE_DELETED"));
    }

    // ===================== GET CASE =====================

    @Test
    void shouldThrowWhenCaseNotFoundById() {

        when(repo.findByIdWithUser(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                caseService.getCaseById(1L)
        );
    }
}