package com.example.casemanagement.dto;

/**
 * DTO för aggregerad statistik per administratör.
 *
 * Används för att överföra sammanställd data till klienten,
 * exempelvis i dashboard-vyer.
 *
 * Innehåller:
 * - namn på administratör
 * - totalt antal ärenden
 * - antal hanterade ärenden
 * - antal väntande ärenden
 *
 * Design:
 * - Immutable via konstruktor (inga setters)
 * - Endast dataöverföring, ingen affärslogik
 */
public class AdminStatsDTO {

    private String name;
    private long total;
    private long handled;
    private long pending;

    public AdminStatsDTO(String name, long total, long handled, long pending) {
        this.name = name;
        this.total = total;
        this.handled = handled;
        this.pending = pending;
    }

    public String getName() { return name; }
    public long getTotal() { return total; }
    public long getHandled() { return handled; }
    public long getPending() { return pending; }
}