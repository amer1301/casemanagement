package com.example.casemanagement.service;

import com.example.casemanagement.exception.ForbiddenException;
import com.example.casemanagement.exception.ResourceNotFoundException;
import com.example.casemanagement.mapper.CaseMapper;
import com.example.casemanagement.model.*;
import com.example.casemanagement.repository.CaseRepository;
import com.example.casemanagement.repository.UserRepository;
import com.example.casemanagement.dto.CreateCaseDTO;
import com.example.casemanagement.dto.UpdateCaseDTO;
import com.example.casemanagement.dto.CaseDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.List;

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

    // ===================== GET ALL =====================

    @Test
    void shouldReturnPagedCases() {

        Case c1 = new Case();
        Case c2 = new Case();

        Page<Case> page = new PageImpl<>(List.of(c1, c2));

        when(repo.searchUnassignedCases(any(), any(), any(Pageable.class)))
                .thenReturn(page);

        when(mapper.toCaseDTO(any())).thenReturn(new CaseDTO());

        Page<CaseDTO> result = caseService.getAll(
                0,
                10,
                "createdAt",
                "desc",
                null,
                null,
                null
        );

        assertEquals(2, result.getContent().size());

        verify(repo).searchUnassignedCases(any(), any(), any(Pageable.class));
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
        when(mapper.toCaseDTO(any())).thenReturn(new CaseDTO());

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
        when(mapper.toCaseDTO(any())).thenReturn(new CaseDTO());

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

    // ===================== UPDATE =====================

    @Test
    void shouldUpdateCaseAndLogAction() {

        Case existing = new Case();

        User user = mock(User.class);
        when(user.getEmail()).thenReturn("test@test.com");

        UpdateCaseDTO dto = new UpdateCaseDTO();
        dto.setTitle("Updated title");
        dto.setDescription("Updated description");

        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(repo.save(any())).thenReturn(existing);
        when(mapper.toCaseDTO(any())).thenReturn(new CaseDTO());

        caseService.update(1L, dto);

        verify(caseLogService).logAction(eq(existing), eq(user), eq("CASE_UPDATED"));
    }

    // ===================== CREATE =====================

    @Test
    void shouldCreateCaseAndLogAction() {

        User user = mock(User.class);
        when(user.getEmail()).thenReturn("test@test.com");

        CreateCaseDTO dto = new CreateCaseDTO();
        dto.setTitle("Test case");
        dto.setDescription("Test description");

        Case caseEntity = new Case();

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(priorityService.determinePriority(dto)).thenReturn(1);
        when(mapper.toCase(dto, user, 1)).thenReturn(caseEntity);
        when(repo.save(caseEntity)).thenReturn(caseEntity);
        when(mapper.toCaseDTO(caseEntity)).thenReturn(new CaseDTO());

        caseService.create(dto);

        verify(caseLogService).logAction(eq(caseEntity), eq(user), eq("CASE_CREATED"));
    }

    // ===================== MY CASES =====================

    @Test
    void shouldReturnOnlyCurrentUsersCases() {

        User user = mock(User.class);
        when(user.getEmail()).thenReturn("test@test.com");

        Case c1 = new Case();
        Case c2 = new Case();

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(repo.findByUser(user)).thenReturn(List.of(c1, c2));
        when(mapper.toCaseDTO(any())).thenReturn(new CaseDTO());

        var result = caseService.getMyCases();

        assertEquals(2, result.size());
        verify(repo).findByUser(user);
    }
}