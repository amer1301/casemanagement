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

    @Test
    void shouldThrowIfNotAdmin() {

        User user = new User();
        user.setRole(Role.USER);

        Case c = new Case();
        c.setStatus(CaseStatus.SUBMITTED);

        assertThrows(ForbiddenException.class, () ->
                service.updateStatus(c, CaseStatus.APPROVED, null, user)
        );
    }
}