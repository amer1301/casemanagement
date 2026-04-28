package com.example.casemanagement.repository;

import com.example.casemanagement.model.CaseNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository för CaseNote-entiteten.
 *
 * Ansvarar för databasanrop relaterade till anteckningar kopplade till ärenden.
 * Bygger på Spring Data JPA, vilket innebär att standardoperationer som
 * save, findById och delete tillhandahålls automatiskt.
 *
 * Design:
 * - Innehåller endast dataåtkomst (ingen affärslogik)
 * - Använder metodnamn för att generera queries automatiskt
 */
public interface CaseNoteRepository extends JpaRepository<CaseNote, Long> {

    /**
     * Hämtar alla anteckningar kopplade till ett specifikt ärende.
     *
     * @param caseId ID för ärendet
     * @return lista med anteckningar
     */
    List<CaseNote> findByCaseEntityId(Long caseId);
}