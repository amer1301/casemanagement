package com.example.casemanagement.repository;

import com.example.casemanagement.model.Case;
import com.example.casemanagement.model.CaseStatus;
import com.example.casemanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

import java.util.List;

public interface CaseRepository extends JpaRepository<Case, Long> {

    List<Case> findByUser(User user);
    List<Case> findByStatus(CaseStatus status);
    List<Case> findByAssignedToIsNull();

    List<Case> findByAssignedTo(User user);
    List<Case> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    boolean existsByUserAndTypeAndStatus(User user, String type, CaseStatus status);

    long countByAssignedToIsNull();
    long countByAssignedToIsNotNull();

    long countByAssignedTo(User user);
    long countByAssignedToAndStatus(User user, CaseStatus status);
    long countByAssignedToAndStatusNot(User user, CaseStatus status);

    @Query("SELECT c FROM Case c JOIN FETCH c.user WHERE c.id = :id")
    Optional<Case> findByIdWithUser(@Param("id") Long id);
}