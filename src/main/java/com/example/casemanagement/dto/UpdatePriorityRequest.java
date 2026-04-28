package com.example.casemanagement.dto;

/**
 * DTO för uppdatering av ärendeprioritet.
 *
 * Representerar en riktad uppdatering av ett specifikt fält (priority).
 *
 * Design:
 * - Separat DTO används istället för generell update för att
 *   begränsa vilka fält som kan ändras från klienten
 * - Prioritet hanteras ofta av administratörer och kräver
 *   därför separat endpoint och behörighetskontroll
 *
 * Syfte:
 * - Förhindra att klienten manipulerar andra delar av ärendet
 * - Möjliggöra tydlig och kontrollerad uppdatering av prioritet
 */
public class UpdatePriorityRequest {

    private Integer priority;

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}