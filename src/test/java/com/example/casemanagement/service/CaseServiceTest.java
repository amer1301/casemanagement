package com.example.casemanagement.service;

import com.example.casemanagement.exception.ForbiddenException;
import com.example.casemanagement.exception.InvalidTransitionException;
import com.example.casemanagement.exception.ResourceNotFoundException;
import com.example.casemanagement.model.*;
import com.example.casemanagement.repository.CaseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaseServiceTest {

    @Mock
    private CaseRepository repo;

    @Mock
    private CaseLogService caseLogService;

    @InjectMocks
    private CaseService service;

    // Hjälpmetoder
    private User adminUser() {
        User u = new User();
        u.setId(1L);
        u.setRole(Role.ADMIN);
        return u;
    }

    private User normalUser() {
        User u = new User();
        u.setId(2L);
        u.setRole(Role.USER);
        return u;
    }

    private Case createCase(User owner) {
        Case c = new Case();
        c.setId(1L);
        c.setStatus(CaseStatus.SUBMITTED);
        c.setUser(owner);
        return c;
    }

    // Test 1
    @Test
    void shouldUpdateStatusSuccessfully() {
        User admin = adminUser();
        User owner = normalUser();

        Case c = createCase(owner);

        when(repo.findById(1L)).thenReturn(Optional.of(c));
        when(repo.save(c)).thenReturn(c);

        CaseService spyService = spy(service);
        doReturn(admin).when(spyService).getCurrentUser();

        spyService.updateStatus(1L, CaseStatus.APPROVED);

        assertEquals(CaseStatus.APPROVED, c.getStatus());
        verify(repo).save(c);
    }

    // Test 2
    @Test
    void shouldThrowIfNotAdmin() {
        User user = normalUser();

        CaseService spyService = spy(service);
        doReturn(user).when(spyService).getCurrentUser();

        assertThrows(ForbiddenException.class, () ->
                spyService.updateStatus(1L, CaseStatus.APPROVED)
        );
    }

    // Test 3
    @Test
    void shouldThrowIfUserTriesToApproveOwnCase() {
        User admin = adminUser();

        Case c = createCase(admin); // samma user

        when(repo.findById(1L)).thenReturn(Optional.of(c));

        CaseService spyService = spy(service);
        doReturn(admin).when(spyService).getCurrentUser();

        assertThrows(ForbiddenException.class, () ->
                spyService.updateStatus(1L, CaseStatus.APPROVED)
        );
    }

    // Test 4
    @Test
    void shouldThrowIfInvalidTransition() {
        User admin = adminUser();

        Case c = new Case();
        c.setId(1L);
        c.setStatus(CaseStatus.APPROVED); // redan klar
        c.setUser(normalUser());

        when(repo.findById(1L)).thenReturn(Optional.of(c));

        CaseService spyService = spy(service);
        doReturn(admin).when(spyService).getCurrentUser();

        assertThrows(InvalidTransitionException.class, () ->
                spyService.updateStatus(1L, CaseStatus.REJECTED)
        );
    }

    // Test 5
    @Test
    void shouldThrowIfCaseNotFound() {
        User admin = adminUser();

        when(repo.findById(1L)).thenReturn(Optional.empty());

        CaseService spyService = spy(service);
        doReturn(admin).when(spyService).getCurrentUser();

        assertThrows(ResourceNotFoundException.class, () ->
                spyService.updateStatus(1L, CaseStatus.APPROVED)
        );
    }
}
