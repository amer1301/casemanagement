package com.example.casemanagement.service;

import com.example.casemanagement.model.Case;
import com.example.casemanagement.model.CaseStatus;
import com.example.casemanagement.repository.CaseRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    private final CaseRepository caseRepository = mock(CaseRepository.class);
    private final ReportService reportService = new ReportService(caseRepository);

    // 🔧 helper
    private Case createCase(CaseStatus status) {
        Case c = new Case();
        c.setStatus(status);
        return c;
    }

    @Test
    void shouldGenerateCorrectMonthlyReport() {

        // Arrange
        List<Case> cases = List.of(
                createCase(CaseStatus.APPROVED),
                createCase(CaseStatus.APPROVED),
                createCase(CaseStatus.SUBMITTED),
                createCase(CaseStatus.REJECTED)
        );

        when(caseRepository.findByCreatedAtBetween(any(), any()))
                .thenReturn(cases);

        // Act
        String result = reportService.generateMonthlyReport();

        // Assert
        assertTrue(result.contains("Totala ärenden,4"));
        assertTrue(result.contains("Hanterade,2"));
        assertTrue(result.contains("Ej hanterade,1"));
        assertTrue(result.contains("Avslagna,1"));
    }

    @Test
    void shouldReturnZeroReportWhenNoCases() {

        // Arrange
        when(caseRepository.findByCreatedAtBetween(any(), any()))
                .thenReturn(List.of());

        // Act
        String result = reportService.generateMonthlyReport();

        // Assert
        assertTrue(result.contains("Totala ärenden,0"));
        assertTrue(result.contains("Hanterade,0"));
        assertTrue(result.contains("Ej hanterade,0"));
        assertTrue(result.contains("Avslagna,0"));
    }

    @Test
    void shouldCallRepositoryWithDateRange() {

        // Arrange
        when(caseRepository.findByCreatedAtBetween(any(), any()))
                .thenReturn(List.of());

        // Act
        reportService.generateMonthlyReport();

        // Assert
        verify(caseRepository).findByCreatedAtBetween(any(), any());
    }
}