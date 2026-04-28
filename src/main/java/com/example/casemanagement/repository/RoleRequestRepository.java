package com.example.casemanagement.repository;

import com.example.casemanagement.model.RoleRequest;
import com.example.casemanagement.model.RoleRequestStatus;
import com.example.casemanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository för RoleRequest-entiteten.
 *
 * Ansvarar för databasanrop kopplade till rollbegäranden.
 * Används som en del av systemets godkännandeprocess (workflow)
 * för rolländringar.
 *
 * Design:
 * - Använder Spring Data JPA för automatisk query-generering
 * - Innehåller valideringsmetoder (exists) för att förhindra duplicerade begäranden
 * - Stödjer filtrering baserat på soft delete
 */
public interface RoleRequestRepository extends JpaRepository<RoleRequest, Long> {

    /**
     * Kontrollerar om en användare redan har en begäran med en viss status.
     *
     * Används för att förhindra att flera identiska begäranden skapas.
     */
    boolean existsByUserAndStatus(User user, RoleRequestStatus status);

    /**
     * Kontrollerar om en användare har en aktiv (ej borttagen) begäran
     * med en viss status.
     *
     * Viktigt vid användning av soft delete.
     */
    boolean existsByUserAndStatusAndDeletedFalse(User user, RoleRequestStatus status);

    /**
     * Hämtar alla rollbegäranden för en specifik användare
     */
    List<RoleRequest> findByUser(User user);

    /**
     * Hämtar alla aktiva (ej borttagna) rollbegäranden
     */
    List<RoleRequest> findByDeletedFalse();

    /**
     * Hämtar aktiva rollbegäranden för en specifik användare
     */
    List<RoleRequest> findByUserAndDeletedFalse(User user);

}