package com.example.casemanagement.controller;

import com.example.casemanagement.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/monthly")
    public ResponseEntity<byte[]> downloadMonthlyReport() {

        String csv = reportService.generateMonthlyReport();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=monthly-report.csv")
                .header("Content-Type", "text/csv")
                .body(csv.getBytes(StandardCharsets.UTF_8));
    }
}