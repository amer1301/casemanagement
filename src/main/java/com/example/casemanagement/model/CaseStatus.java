package com.example.casemanagement.model;

/**
 * Enum som representerar ett ärendes status i systemet.
 *
 * Definierar de tillstånd som ett ärende kan befinna sig i
 * under sin livscykel.
 *
 * Design:
 * - Används tillsammans med en state machine (CaseStatusTransition)
 *   för att kontrollera tillåtna övergångar
 * - Lagring sker som STRING i databasen via @Enumerated
 *
 * Statusflöde:
 * - SUBMITTED → initialt tillstånd
 * - APPROVED → ärendet har godkänts
 * - REJECTED → ärendet har avslagits
 *
 * Syfte:
 * - Säkerställa att endast giltiga statusvärden används
 * - Möjliggöra kontroll av affärsregler kring statusändringar
 */
public enum CaseStatus {

    SUBMITTED,
    APPROVED,
    REJECTED
}