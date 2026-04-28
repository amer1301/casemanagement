package com.example.casemanagement.model;

/**
 * Enum som representerar användarroller i systemet.
 *
 * Används för att styra behörigheter och åtkomst till olika delar
 * av applikationen genom rollbaserad accesskontroll.
 *
 * Roller:
 * - USER: kan skapa och hantera sina egna ärenden
 * - ADMIN: kan handlägga ärenden och fatta beslut
 * - MANAGER: har utökade rättigheter, exempelvis godkänna admins
 *
 * Design:
 * - Enum används för att säkerställa typ-säkerhet och undvika ogiltiga roller
 * - Används tillsammans med Spring Security (@PreAuthorize, SecurityConfig)
 *
 * Syfte:
 * - Centralisera rollhantering
 * - Möjliggöra tydlig och säker behörighetskontroll
 */
public enum Role {

    USER,
    ADMIN,
    MANAGER
}