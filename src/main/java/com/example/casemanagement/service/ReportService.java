package com.example.casemanagement.service;

import com.example.casemanagement.model.Case;
import com.example.casemanagement.repository.CaseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {

    private final CaseRepository caseRepository;

    public ReportService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    public String generateMonthlyReport() {
        List<Case> cases = caseRepository.findAll();

        LocalDate now = LocalDate.now();

        List<Case> monthlyCases = cases.stream()
                .filter(c -> c.getCreatedAt() != null)
                .filter(c -> c.getCreatedAt().getMonth() == now.getMonth()
                        && c.getCreatedAt().getYear() == now.getYear())
                .toList();

        long total = monthlyCases.size();

        long handled = monthlyCases.stream()
                .filter(c -> c.getStatus().name().equals("APPROVED"))
                .count();

        long pending = monthlyCases.stream()
                .filter(c -> c.getStatus().name().equals("SUBMITTED"))
                .count();

        long rejected = monthlyCases.stream()
                .filter(c -> c.getStatus().name().equals("REJECTED"))
                .count();

        // CSV-format
        return "Typ,Antal\n" +
                "Totala ärenden," + total + "\n" +
                "Hanterade," + handled + "\n" +
                "Ej hanterade," + pending + "\n" +
                "Avslagna," + rejected + "\n";
    }
}
