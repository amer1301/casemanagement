package com.example.casemanagement.service;

import com.example.casemanagement.model.Case;
import com.example.casemanagement.model.CaseStatus;
import com.example.casemanagement.model.User;
import com.example.casemanagement.repository.CaseRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.Mockito.*;

class CaseServiceTest {

    private final CaseRepository repo = mock(CaseRepository.class);
    private final CaseStatusService statusService = mock(CaseStatusService.class);
    private final RoleRequestService roleRequestService = mock(RoleRequestService.class);
    private final CasePriorityService priorityService = mock(CasePriorityService.class);

    private final CaseService caseService = new CaseService(
            repo, null, null, null, statusService, roleRequestService, priorityService
    );

    @Test
    void shouldDelegateStatusUpdate() {

        Case c = new Case();
        User user = new User();

        when(repo.findById(1L)).thenReturn(Optional.of(c));
        when(statusService.updateStatus(any(), any(), any(), any()))
                .thenReturn(c);

        caseService.updateStatus(1L, CaseStatus.APPROVED, "ok");

        verify(statusService).updateStatus(any(), any(), any(), any());
    }
}