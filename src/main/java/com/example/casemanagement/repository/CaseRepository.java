package com.example.casemanagement.repository;

import com.example.casemanagement.model.Case;
import com.example.casemanagement.model.CaseStatus;
import com.example.casemanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Repository för Case-entiteten.
 *
 * Ansvarar för all databashantering kopplad till ärenden.
 * Bygger på Spring Data JPA vilket innebär att standardoperationer
 * (CRUD) genereras automatiskt.
 *
 * Design:
 * - Innehåller endast dataåtkomst (ingen affärslogik)
 * - Utnyttjar metodnamn för att generera queries
 * - Innehåller även specialanpassade queries vid behov (t.ex. sökning och filtrering)
 */
public interface CaseRepository extends JpaRepository<Case, Long> {

    /**
     * Hämtar alla ärenden skapade av en specifik användare
     */
    List<Case> findByUser(User user);

    /**
     * Hämtar ärenden baserat på status
     */
    Page<Case> findByStatus(CaseStatus status, Pageable pageable);

    /**
     * Hämtar alla ej tilldelade ärenden
     */
    List<Case> findByAssignedToIsNull();

    /**
     * Hämtar ärenden tilldelade en specifik användare (t.ex. admin)
     */
    List<Case> findByAssignedTo(User user);

    /**
     * Hämtar ärenden inom ett datumintervall
     * Används exempelvis för rapportgenerering
     */
    List<Case> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Räknar antal ej tilldelade ärenden
     */
    long countByAssignedToIsNull();

    /**
     * Räknar antal tilldelade ärenden
     */
    long countByAssignedToIsNotNull();

    /**
     * Räknar antal ärenden tilldelade en specifik användare
     */
    long countByAssignedTo(User user);

    /**
     * Räknar antal ärenden med viss status för en användare
     */
    long countByAssignedToAndStatus(User user, CaseStatus status);

    /**
     * Räknar antal ärenden som inte har en viss status
     */
    long countByAssignedToAndStatusNot(User user, CaseStatus status);

    /**
     * Hämtar ett ärende inklusive dess kopplade användare.
     * <p>
     * JOIN FETCH används för att:
     * - undvika LazyInitializationException
     * - optimera prestanda genom att hämta relationen i samma query
     */
    @Query("SELECT c FROM Case c JOIN FETCH c.user WHERE c.id = :id")
    Optional<Case> findByIdWithUser(@Param("id") Long id);

    /**
     * Söker ärenden med stöd för:
     * - filtrering på status
     * - fritextsökning (titel, beskrivning, namn, personnummer)
     * - pagination via Pageable
     * <p>
     * Denna metod används för avancerad listning i frontend (filter + search).
     */
    @Query("""
            SELECT c FROM Case c
            WHERE c.assignedTo IS NULL
            AND (:status IS NULL OR c.status = :status)
            AND (
              :q IS NULL OR :q = '' OR
              LOWER(c.title) LIKE CONCAT('%', LOWER(:q), '%') OR
              LOWER(c.description) LIKE CONCAT('%', LOWER(:q), '%') OR
              LOWER(c.applicantName) LIKE CONCAT('%', LOWER(:q), '%') OR
              c.personalNumber LIKE CONCAT('%', :q, '%')
            )
            """)
    Page<Case> searchUnassignedCases(
            @Param("status") CaseStatus status,
            @Param("q") String q,
            Pageable pageable
    );

    /**
     * Hämtar alla ärenden (manager)
     */
    @Query("""
SELECT c FROM Case c
WHERE (:status IS NULL OR c.status = :status)
AND (
  :q IS NULL OR :q = '' OR
  LOWER(c.title) LIKE CONCAT('%', LOWER(:q), '%')
)
AND (
  :assignedTo IS NULL OR
  (:assignedTo = -1 AND c.assignedTo IS NULL) OR
  c.assignedTo.id = :assignedTo
)
""")
    Page<Case> searchAllCases(
            @Param("status") CaseStatus status,
            @Param("q") String q,
            @Param("assignedTo") Long assignedTo,
            Pageable pageable
    );
}