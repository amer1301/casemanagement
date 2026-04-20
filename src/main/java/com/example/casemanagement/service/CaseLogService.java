package com.example.casemanagement.service;

import com.example.casemanagement.dto.CaseLogDTO;
import com.example.casemanagement.mapper.CaseMapper;
import com.example.casemanagement.model.Case;
import com.example.casemanagement.model.CaseLog;
import com.example.casemanagement.model.User;
import com.example.casemanagement.repository.CaseLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CaseLogService {

    private final CaseLogRepository repo;
    private final CaseMapper mapper;

    public CaseLogService(CaseLogRepository repo, CaseMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public void logAction(Case c, User user, String action) {

        // 1. Mappa
        CaseLog log = mapper.toCaseLog(c, user, action);

        // 2. Spara
        repo.save(log);
    }

    public List<CaseLogDTO> getLogs(Long caseId) {

        return repo.findByCaseEntityId(caseId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }
}