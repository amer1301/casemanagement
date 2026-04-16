package com.example.casemanagement.repository;

import com.example.casemanagement.model.CaseNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaseNoteRepository extends JpaRepository<CaseNote, Long> {

    List<CaseNote> findByCaseEntityId(Long caseId);
}
