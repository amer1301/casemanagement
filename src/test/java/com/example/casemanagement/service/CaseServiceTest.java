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

    private User mockUser;

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

        mockUser = mock(User.class);
        when(mockUser.getEmail()).thenReturn("test@test.com");

        when(mockUser.getRole()).thenReturn(Role.ADMIN);

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(mockUser));
    }

    // ===================== GET ALL =====================

    @Test
    void shouldReturnPagedCases() {

        Case c1 = new Case();
        c1.setUser(mockUser);

        Case c2 = new Case();
        c2.setUser(mockUser);

        Page<Case> page = new PageImpl<>(List.of(c1, c2));

        when(repo.searchUnassignedCases(any(), any(), any(Pageable.class)))
                .thenReturn(page);

        when(mapper.toCaseDTO(c1)).thenReturn(new CaseDTO());
        when(mapper.toCaseDTO(c2)).thenReturn(new CaseDTO());

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

        when(repo.findById(1L)).thenReturn(Optional.of(c));
        when(statusService.updateStatus(any(), any(), any(), any())).thenReturn(c);
        when(mapper.toCaseDTO(any())).thenReturn(new CaseDTO());

        caseService.updateStatus(1L, CaseStatus.APPROVED, "ok");

        verify(statusService).updateStatus(eq(c), eq(CaseStatus.APPROVED), eq("ok"), eq(mockUser));
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

        when(mockUser.getId()).thenReturn(1L);

        Case c = new Case();
        c.setUser(owner);

        when(repo.findById(10L)).thenReturn(Optional.of(c));
        when(repo.save(any())).thenReturn(c);
        when(mapper.toCaseDTO(any())).thenReturn(new CaseDTO());

        caseService.assignToCurrentUser(10L);

        assertEquals(mockUser, c.getAssignedTo());

        verify(notificationService).createNotification(
                eq(owner),
                contains("tilldelats"),
                any()
        );
    }

    @Test
    void shouldNotAllowAssigningOwnCase() {

        when(mockUser.getId()).thenReturn(1L);

        Case c = new Case();
        c.setUser(mockUser);

        when(repo.findById(1L)).thenReturn(Optional.of(c));

        assertThrows(ForbiddenException.class, () ->
                caseService.assignToCurrentUser(1L)
        );
    }

    // ===================== DELETE =====================

    @Test
    void shouldDeleteCaseAndLog() {

        Case c = new Case();

        when(repo.findById(1L)).thenReturn(Optional.of(c));

        caseService.deleteCase(1L);

        verify(repo).delete(c);
        verify(caseLogService).logAction(eq(c), eq(mockUser), eq("CASE_DELETED"));
    }

    // ===================== UPDATE =====================

    @Test
    void shouldUpdateCaseAndLogAction() {

        Case existing = new Case();

        UpdateCaseDTO dto = new UpdateCaseDTO();
        dto.setTitle("Updated title");
        dto.setDescription("Updated description");

        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenReturn(existing);
        when(mapper.toCaseDTO(any())).thenReturn(new CaseDTO());

        caseService.update(1L, dto);

        verify(caseLogService).logAction(eq(existing), eq(mockUser), eq("CASE_UPDATED"));
    }

    // ===================== CREATE =====================

    @Test
    void shouldCreateCaseAndLogAction() {

        CreateCaseDTO dto = new CreateCaseDTO();
        dto.setTitle("Test case");
        dto.setDescription("Test description");

        Case caseEntity = new Case();

        when(priorityService.determinePriority(dto)).thenReturn(1);
        when(mapper.toCase(dto, mockUser, 1)).thenReturn(caseEntity);
        when(repo.save(caseEntity)).thenReturn(caseEntity);
        when(mapper.toCaseDTO(caseEntity)).thenReturn(new CaseDTO());

        caseService.create(dto);

        verify(caseLogService).logAction(eq(caseEntity), eq(mockUser), eq("CASE_CREATED"));
    }

    // ===================== MY CASES =====================

    @Test
    void shouldReturnOnlyCurrentUsersCases() {

        Case c1 = new Case();
        c1.setAssignedTo(mockUser);

        Case c2 = new Case();
        c2.setAssignedTo(mockUser);

        when(repo.findByAssignedTo(mockUser)).thenReturn(List.of(c1, c2));

        when(mapper.toCaseDTO(c1)).thenReturn(new CaseDTO());
        when(mapper.toCaseDTO(c2)).thenReturn(new CaseDTO());

        var result = caseService.getMyCases();

        assertEquals(2, result.size());

        verify(repo).findByAssignedTo(mockUser);
    }
}