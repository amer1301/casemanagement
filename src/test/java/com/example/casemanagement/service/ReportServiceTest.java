package com.example.casemanagement.service;

import com.example.casemanagement.model.Case;
import com.example.casemanagement.model.CaseStatus;
import com.example.casemanagement.repository.CaseRepository;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    private final CaseRepository repo = mock(CaseRepository.class);
    private final ReportService service = new ReportService(repo);

    // ===================== HELPERS =====================

    private Case createCase(CaseStatus status) {
        Case c = new Case();
        c.setStatus(status);
        return c;
    }

    // ===================== FULL REPORT =====================

    @Test
    void shouldGenerateCorrectMonthlyReport() {

        List<Case> cases = List.of(
                createCase(CaseStatus.APPROVED),
                createCase(CaseStatus.APPROVED),
                createCase(CaseStatus.SUBMITTED),
                createCase(CaseStatus.REJECTED)
        );

        when(repo.findByCreatedAtBetween(any(), any()))
                .thenReturn(cases);

        String result = service.generateMonthlyReport();

        String expected =
                "Typ,Antal\n" +
                        "Totala ärenden,4\n" +
                        "Hanterade,2\n" +
                        "Ej hanterade,1\n" +
                        "Avslagna,1\n";

        assertEquals(expected, result);
    }

    // ===================== EMPTY =====================

    @Test
    void shouldReturnZeroReportWhenNoCases() {

        when(repo.findByCreatedAtBetween(any(), any()))
                .thenReturn(List.of());

        String result = service.generateMonthlyReport();

        String expected =
                "Typ,Antal\n" +
                        "Totala ärenden,0\n" +
                        "Hanterade,0\n" +
                        "Ej hanterade,0\n" +
                        "Avslagna,0\n";

        assertEquals(expected, result);
    }

    // ===================== ONLY APPROVED =====================

    @Test
    void shouldHandleOnlyApprovedCases() {

        List<Case> cases = List.of(
                createCase(CaseStatus.APPROVED),
                createCase(CaseStatus.APPROVED)
        );

        when(repo.findByCreatedAtBetween(any(), any()))
                .thenReturn(cases);

        String result = service.generateMonthlyReport();

        assertTrue(result.contains("Hanterade,2"));
        assertTrue(result.contains("Ej hanterade,0"));
        assertTrue(result.contains("Avslagna,0"));
    }

    // ===================== ONLY SUBMITTED =====================

    @Test
    void shouldHandleOnlyPendingCases() {

        List<Case> cases = List.of(
                createCase(CaseStatus.SUBMITTED),
                createCase(CaseStatus.SUBMITTED)
        );

        when(repo.findByCreatedAtBetween(any(), any()))
                .thenReturn(cases);

        String result = service.generateMonthlyReport();

        assertTrue(result.contains("Ej hanterade,2"));
        assertTrue(result.contains("Hanterade,0"));
        assertTrue(result.contains("Avslagna,0"));
    }

    // ===================== DATE RANGE =====================

    @Test
    void shouldCallRepositoryWithDateRange() {

        when(repo.findByCreatedAtBetween(any(), any()))
                .thenReturn(List.of());

        service.generateMonthlyReport();

        ArgumentCaptor<LocalDateTime> startCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> endCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

        verify(repo).findByCreatedAtBetween(startCaptor.capture(), endCaptor.capture());

        LocalDateTime start = startCaptor.getValue();
        LocalDateTime end = endCaptor.getValue();

        assertNotNull(start);
        assertNotNull(end);

        // sanity check: start ska vara före end
        assertTrue(start.isBefore(end));
    }
}