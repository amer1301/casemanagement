package com.example.casemanagement.model;

/**
 * Enum som representerar olika kategorier av ärenden.
 *
 * Används för att klassificera ärenden i systemet och
 * möjliggöra strukturering, filtrering och prioritering.
 *
 * Design:
 * - Enum används istället för String för att säkerställa typ-säkerhet
 *   och förhindra ogiltiga värden
 * - Lagring sker som STRING i databasen via @Enumerated i entiteten
 *
 * Exempel:
 * - STUDY: studierelaterade ansökningar
 * - HOUSING: bostadsrelaterade ärenden
 * - SICKNESS_BENEFIT: sjukpenning
 */
public enum CaseCategory {

    STUDY,
    HOUSING,
    SICKNESS_BENEFIT,
    PARENTAL_LEAVE,
    UNEMPLOYMENT_SUPPORT
}