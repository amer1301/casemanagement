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
 * - Innehåller även specialanpassade queries vid behov
 */
public interface CaseRepository extends JpaRepository<Case, Long> {

    /**
     * Hämtar alla ärenden skapade av en specifik användare
     */
    List<Case> findByUser(User user);

    /**
     * Hämtar ärenden baserat på status
     */
    List<Case> findByStatus(CaseStatus status);

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
     *
     * JOIN FETCH används för att:
     * - undvika LazyInitializationException
     * - optimera prestanda genom att hämta relationen i samma query
     */
    @Query("SELECT c FROM Case c JOIN FETCH c.user WHERE c.id = :id")
    Optional<Case> findByIdWithUser(@Param("id") Long id);
}