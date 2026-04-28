package com.example.casemanagement.domain;

import com.example.casemanagement.exception.InvalidTransitionException;
import com.example.casemanagement.model.CaseStatus;

import java.util.List;
import java.util.Map;

/**
 * Representerar tillåtna statusövergångar för ett ärende.
 *
 * Denna klass implementerar en enkel state machine där varje status
 * definierar vilka efterföljande statusar som är tillåtna.
 *
 * Syfte:
 * - Centralisera regler för statusändringar
 * - Förhindra ogiltiga tillstånd i systemet
 * - Göra affärslogik explicit och lätt att underhålla
 *
 * Design:
 * - Immutable struktur (Map.of) för att undvika oavsiktliga förändringar
 * - Statisk valideringsmetod för enkel användning i service-lagret
 */
public class CaseStatusTransition {

    /**
     * Definierar tillåtna övergångar mellan statusar.
     *
     * Exempel:
     * - SUBMITTED → APPROVED eller REJECTED
     * - APPROVED → inga vidare övergångar (slutstatus)
     * - REJECTED → inga vidare övergångar (slutstatus)
     */
    private static final Map<CaseStatus, List<CaseStatus>> transitions = Map.of(
            CaseStatus.SUBMITTED, List.of(CaseStatus.APPROVED, CaseStatus.REJECTED),
            CaseStatus.APPROVED, List.of(),
            CaseStatus.REJECTED, List.of()
    );

    /**
     * Validerar att en statusövergång är tillåten enligt definierade regler.
     *
     * Kastar InvalidTransitionException om:
     * - någon status är null
     * - övergången inte finns i tillåtna transitions
     *
     * Denna metod används i service-lagret för att säkerställa att
     * systemet alltid befinner sig i ett giltigt tillstånd.
     */
    public static void validate(CaseStatus from, CaseStatus to) {

        // Skydd mot ogiltiga indata
        if (from == null || to == null) {
            throw new InvalidTransitionException("Status cannot be null");
        }

        // Kontrollera att övergången är definierad i state machine
        if (!transitions.getOrDefault(from, List.of()).contains(to)) {
            throw new InvalidTransitionException(
                    "Invalid transition: " + from + " -> " + to
            );
        }
    }
}