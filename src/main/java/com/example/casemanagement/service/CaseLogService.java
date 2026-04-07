package com.example.casemanagement.service;

import com.example.casemanagement.model.Case;
import com.example.casemanagement.model.CaseLog;
import com.example.casemanagement.dto.CaseLogDTO;
import com.example.casemanagement.model.User;
import com.example.casemanagement.repository.CaseLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CaseLogService {

    private final CaseLogRepository repo;

    public CaseLogService(CaseLogRepository repo) {
        this.repo = repo;
    }

    public void logAction(Case c, User user, String action) {
        CaseLog log = new CaseLog();
        log.setCaseEntity(c);
        log.setUser(user);
        log.setAction(action);
        log.setTimestamp(LocalDateTime.now());

        repo.save(log);
    }

    public List<CaseLogDTO> getLogs(Long caseId, Long userId) {
        List<CaseLog> logs;

        if (userId != null) {
            logs = repo.findByCaseEntityIdAndUserId(caseId, userId);
        } else {
            logs = repo.findByCaseEntityId(caseId);
        }

        return logs.stream()
                .map(log -> new CaseLogDTO(
                        log.getId(),
                        log.getAction(),
                        log.getTimestamp(),
                        log.getCaseEntity().getId(),
                        log.getUser().getEmail()
                ))
                .toList();
    }
}
