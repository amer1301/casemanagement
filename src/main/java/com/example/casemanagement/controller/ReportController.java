package com.example.casemanagement.controller;

import com.example.casemanagement.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.casemanagement.dto.AdminStatsDTO;
import com.example.casemanagement.repository.UserRepository;

import java.nio.charset.StandardCharsets;

/**
 * Controller för generering och nedladdning av rapporter.
 *
 * Denna controller exponerar funktionalitet för att exportera systemdata
 * i ett externt format (CSV), vilket är vanligt i administrativa system.
 *
 * All logik för datainsamling och aggregering hanteras i service-lagret.
 */
@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Genererar och returnerar en månadsrapport i CSV-format.
     *
     * Säkerhet:
     * - Endast användare med rollen MANAGER har åtkomst
     *
     * Implementation:
     * - Data hämtas och bearbetas i service-lagret
     * - Resultatet returneras som en nedladdningsbar fil via HTTP-response
     *
     * HTTP headers:
     * - Content-Disposition: anger att svaret ska laddas ner som fil
     * - Content-Type: specificerar filformatet (text/csv)
     */
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/monthly")
    public ResponseEntity<byte[]> downloadMonthlyReport() {

        String csv = reportService.generateMonthlyReport();

        return ResponseEntity.ok()
                // Gör att webbläsaren laddar ner filen istället för att visa den
                .header("Content-Disposition", "attachment; filename=monthly-report.csv")

                // Anger MIME-typ för CSV
                .header("Content-Type", "text/csv")

                // Konverterar sträng till byte-array för filöverföring
                .body(csv.getBytes(StandardCharsets.UTF_8));
    }
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/admin-stats")
    public List<AdminStatsDTO> getAdminStats() {
        return reportService.getAdminStats();
    }
}