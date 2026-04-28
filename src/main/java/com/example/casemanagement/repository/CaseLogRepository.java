package com.example.casemanagement.repository;

import com.example.casemanagement.model.CaseLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository för CaseLog-entiteten.
 *
 * Ansvarar för databasanrop kopplade till loggar för ärenden.
 * Bygger på Spring Data JPA vilket innebär att standard-CRUD
 * operationer tillhandahålls automatiskt.
 *
 * Design:
 * - Innehåller endast databasåtkomst (ingen affärslogik)
 * - Använder metodnamn för att automatiskt generera queries
 */
public interface CaseLogRepository extends JpaRepository<CaseLog, Long> {

    /**
     * Hämtar alla loggar för ett specifikt ärende.
     *
     * @param caseId ID för ärendet
     * @return lista med loggar kopplade till ärendet
     */
    List<CaseLog> findByCaseEntityId(Long caseId);

    /**
     * Hämtar loggar för ett specifikt ärende och en specifik användare.
     *
     * Används exempelvis för filtrering eller spårning av
     * en enskild användares aktiviteter.
     *
     * @param caseId ID för ärendet
     * @param userId ID för användaren
     * @return lista med matchande loggar
     */
    List<CaseLog> findByCaseEntityIdAndUser_Id(Long caseId, Long userId);
}