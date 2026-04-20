package com.example.casemanagement.service;

import com.example.casemanagement.model.Case;
import com.example.casemanagement.repository.CaseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {

    private final CaseRepository caseRepository;

    public ReportService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    public String generateMonthlyReport() {

        LocalDate now = LocalDate.now();

        LocalDateTime start = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59);

        List<Case> monthlyCases = caseRepository.findByCreatedAtBetween(start, end);

        long total = monthlyCases.size();

        long handled = 0;
        long pending = 0;
        long rejected = 0;

        for (Case c : monthlyCases) {
            switch (c.getStatus()) {
                case APPROVED -> handled++;
                case SUBMITTED -> pending++;
                case REJECTED -> rejected++;
            }
        }

        return "Typ,Antal\n" +
                "Totala ärenden," + total + "\n" +
                "Hanterade," + handled + "\n" +
                "Ej hanterade," + pending + "\n" +
                "Avslagna," + rejected + "\n";
    }
}
