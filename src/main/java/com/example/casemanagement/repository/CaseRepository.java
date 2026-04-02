package com.example.casemanagement.repository;

import com.example.casemanagement.model.Case;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaseRepository extends JpaRepository<Case, Long> {
    List<Case> findByUserEmail(String email);
}
