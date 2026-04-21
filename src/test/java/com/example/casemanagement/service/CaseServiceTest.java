package com.example.casemanagement.service;

import com.example.casemanagement.mapper.CaseMapper;
import com.example.casemanagement.model.Case;
import com.example.casemanagement.model.CaseStatus;
import com.example.casemanagement.model.Role;
import com.example.casemanagement.model.User;
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

    private final CaseRepository repo = mock(CaseRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final CaseLogService caseLogService = mock(CaseLogService.class);
    private final CaseMapper mapper = mock(CaseMapper.class);
    private final CaseStatusService statusService = mock(CaseStatusService.class);
    private final RoleRequestService roleRequestService = mock(RoleRequestService.class);
    private final CasePriorityService priorityService = mock(CasePriorityService.class);

    private final CaseService caseService = new CaseService(
            repo,
            userRepository,
            caseLogService,
            mapper,
            statusService,
            roleRequestService,
            priorityService
    );

    @BeforeEach
    void setupSecurity() {
        SecurityContext context = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);

        when(auth.getName()).thenReturn("test@test.com");
        when(context.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(context);
    }

    @Test
    void shouldDelegateStatusUpdate() {

        Case c = new Case();
        User user = new User("Test", "test@test.com", "pass", Role.ADMIN);

        when(repo.findById(1L)).thenReturn(Optional.of(c));
        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(statusService.updateStatus(any(), any(), any(), any()))
                .thenReturn(c);

        when(mapper.toCaseDTO(any())).thenReturn(null); // vi bryr oss inte om DTO här

        caseService.updateStatus(1L, CaseStatus.APPROVED, "ok");

        verify(statusService).updateStatus(eq(c), eq(CaseStatus.APPROVED), eq("ok"), eq(user));
    }

    @Test
    void shouldThrowIfCaseNotFound() {

        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                caseService.updateStatus(1L, CaseStatus.APPROVED, "ok")
        );
    }
}