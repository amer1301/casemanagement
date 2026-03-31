package com.example.casemanagement.repository;

import com.example.casemanagement.model.Case;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseRepository extends JpaRepository<Case, Long> {
}
