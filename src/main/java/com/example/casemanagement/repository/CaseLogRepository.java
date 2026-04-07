package com.example.casemanagement.repository;

import com.example.casemanagement.model.CaseLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaseLogRepository extends JpaRepository<CaseLog, Long> {
    List<CaseLog> findByCaseEntityId(Long caseId);

    List<CaseLog> findByCaseEntityIdAndUserId(Long caseId, Long userId);
}
