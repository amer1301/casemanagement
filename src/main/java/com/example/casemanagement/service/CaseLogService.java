package com.example.casemanagement.service;

import com.example.casemanagement.dto.CaseLogDTO;
import com.example.casemanagement.mapper.CaseMapper;
import com.example.casemanagement.model.Case;
import com.example.casemanagement.model.CaseLog;
import com.example.casemanagement.model.User;
import com.example.casemanagement.repository.CaseLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service för hantering av loggar kopplade till ärenden.
 *
 * Ansvar:
 * - Skapa loggposter vid viktiga händelser
 * - Hämta loggar för ett specifikt ärende
 *
 * Design:
 * - Separerad från CaseService för att hålla logik modulär
 * - Använder mapper för konvertering mellan Entity och DTO
 * - Innehåller ingen HTTP-logik (hanteras av controller)
 */
@Service
public class CaseLogService {

    private final CaseLogRepository repo;
    private final CaseMapper mapper;

    public CaseLogService(CaseLogRepository repo, CaseMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    /**
     * Skapar och sparar en loggpost.
     *
     * Flöde:
     * 1. Mappa data till CaseLog-entitet
     * 2. Spara i databasen
     *
     * Används exempelvis vid:
     * - skapande av ärende
     * - statusändringar
     * - borttagning
     */
    public void logAction(Case c, User user, String action) {

        // 1. Konvertera till CaseLog-entitet
        CaseLog log = mapper.toCaseLog(c, user, action);

        // 2. Spara loggpost
        repo.save(log);
    }

    /**
     * Hämtar alla loggar för ett specifikt ärende.
     *
     * Flöde:
     * 1. Hämta loggar från databasen
     * 2. Mappa till DTO
     */
    public List<CaseLogDTO> getLogs(Long caseId) {

        return repo.findByCaseEntityId(caseId)
                .stream()
                .map(mapper::toCaseLogDTO)
                .toList();
    }
}