package com.example.casemanagement.model;

import com.example.casemanagement.exception.InvalidTransitionException;

import java.util.List;
import java.util.Map;

/**
 * Hanterar tillåtna statusövergångar för ärenden (state machine).
 *
 * Definierar vilka statusförändringar som är giltiga i systemet
 * och används för att säkerställa att ärendets livscykel följer
 * affärsreglerna.
 *
 * Design:
 * - Implementerad som en enkel state machine med hjälp av en Map
 * - Nyckel = nuvarande status
 * - Värde = lista över tillåtna nästa statusar
 *
 * Syfte:
 * - Förhindra ogiltiga statusändringar
 * - Centralisera logik för statusövergångar
 * - Göra systemet mer robust och lätt att underhålla
 *
 * Exempel:
 * - SUBMITTED → APPROVED eller REJECTED (tillåtet)
 * - APPROVED → SUBMITTED (inte tillåtet)
 */
public class CaseStatusTransition {

    /**
     * Definierar alla tillåtna statusövergångar i systemet.
     */
    private static final Map<CaseStatus, List<CaseStatus>> transitions = Map.of(
            CaseStatus.SUBMITTED, List.of(CaseStatus.APPROVED, CaseStatus.REJECTED),
            CaseStatus.APPROVED, List.of(),
            CaseStatus.REJECTED, List.of()
    );

    /**
     * Validerar en statusövergång.
     *
     * @param from nuvarande status
     * @param to ny status
     *
     * @throws InvalidTransitionException om övergången inte är tillåten
     */
    public static void validate(CaseStatus from, CaseStatus to) {

        // Kontrollera om den nya statusen finns i listan över tillåtna övergångar
        if (!transitions.getOrDefault(from, List.of()).contains(to)) {

            throw new InvalidTransitionException(
                    "Invalid transition: " + from + " -> " + to
            );
        }
    }
}