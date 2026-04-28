package com.example.casemanagement.model;

/**
 * Enum som representerar status för en rollbegäran.
 *
 * Används för att modellera processen där en användare ansöker
 * om en ny roll och denna begäran behandlas av en ansvarig (MANAGER).
 *
 * Statusflöde:
 * - PENDING: begäran är skapad och väntar på beslut
 * - APPROVED: begäran har godkänts
 * - REJECTED: begäran har avslagits
 *
 * Design:
 * - Används tillsammans med RoleRequest för att representera ett workflow
 * - Säkerställer att endast giltiga statusvärden kan användas
 *
 * Syfte:
 * - Möjliggöra kontrollerad hantering av rolländringar
 * - Skilja denna process från direkt uppdatering av användarroller
 */
public enum RoleRequestStatus {

    PENDING,
    APPROVED,
    REJECTED
}