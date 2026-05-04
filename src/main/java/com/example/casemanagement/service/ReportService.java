package com.example.casemanagement.service;

import com.example.casemanagement.model.Case;
import com.example.casemanagement.repository.CaseRepository;
import org.springframework.stereotype.Service;
import com.example.casemanagement.repository.UserRepository;
import com.example.casemanagement.dto.AdminStatsDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service för generering av rapporter.
 *
 * Ansvar:
 * - Samla in data från databasen
 * - Bearbeta och aggregera statistik
 * - Returnera rapport i CSV-format
 *
 * Design:
 * - Separerad från CaseService för tydlig ansvarsfördelning
 * - Använder repository för datahämtning
 * - Enkel CSV-generering för export
 */
@Service
public class ReportService {

    private final CaseRepository caseRepository;
    private final UserRepository userRepository;

    public ReportService(CaseRepository caseRepository,
                         UserRepository userRepository) {
        this.caseRepository = caseRepository;
        this.userRepository = userRepository;
    }

        public List<AdminStatsDTO> getAdminStats() {

            List<Case> cases = caseRepository.findAll();

            return userRepository.findAll().stream()
                    .filter(user -> user.getRole().name().equals("ADMIN"))
                    .map(admin -> {

                        long total = cases.stream()
                                .filter(c -> admin.equals(c.getAssignedTo()))
                                .count();

                        long handled = cases.stream()
                                .filter(c -> admin.equals(c.getAssignedTo()))
                                .filter(c -> c.getStatus().name().equals("APPROVED"))
                                .count();

                        long pending = cases.stream()
                                .filter(c -> admin.equals(c.getAssignedTo()))
                                .filter(c -> c.getStatus().name().equals("SUBMITTED"))
                                .count();

                        return new AdminStatsDTO(
                                admin.getName(),
                                total,
                                handled,
                                pending
                        );
                    })
                    .collect(Collectors.toList());
        }

    /**
     * Genererar en månadsrapport över ärenden.
     *
     * Flöde:
     * 1. Beräkna start- och slutdatum för aktuell månad
     * 2. Hämta ärenden inom intervallet
     * 3. Räkna antal per status
     * 4. Returnera resultat i CSV-format
     */
    public String generateMonthlyReport() {

        // Nuvarande datum
        LocalDate now = LocalDate.now();

        // Start: första dagen i månaden
        LocalDateTime start = now.withDayOfMonth(1).atStartOfDay();

        // Slut: sista dagen i månaden
        LocalDateTime end = now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59);

        // Hämta ärenden för aktuell månad
        List<Case> monthlyCases = caseRepository.findByCreatedAtBetween(start, end);

        long total = monthlyCases.size();

        long handled = 0;
        long pending = 0;
        long rejected = 0;

        // Aggregera statistik baserat på status
        for (Case c : monthlyCases) {
            switch (c.getStatus()) {
                case APPROVED -> handled++;
                case SUBMITTED -> pending++;
                case REJECTED -> rejected++;
            }
        }

        // Bygg CSV-sträng
        return "Typ,Antal\n" +
                "Totala ärenden," + total + "\n" +
                "Hanterade," + handled + "\n" +
                "Ej hanterade," + pending + "\n" +
                "Avslagna," + rejected + "\n";
    }
}