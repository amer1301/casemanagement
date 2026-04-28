package com.example.casemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO för uppdatering av ärenden.
 *
 * Representerar de fält som en användare har rätt att ändra
 * efter att ett ärende har skapats.
 *
 * Validering:
 * - title och description är obligatoriska
 * - längd begränsas för att säkerställa datakvalitet
 *
 * Design:
 * - Endast ett urval av fält exponeras (titel och beskrivning)
 * - Kritiska fält som status, prioritet och användare hanteras separat
 *   i service-lagret för att skydda affärsregler
 *
 * Syfte:
 * - Förhindra att klienten uppdaterar känsliga eller systemstyrda fält
 * - Upprätthålla tydlig ansvarsfördelning mellan API och affärslogik
 */
public class UpdateCaseDTO {

    @NotBlank(message = "Titeln får inte vara tom")
    @Size(min = 3, max = 100)
    private String title;

    @NotBlank(message = "Beskrivningen får inte var tom")
    @Size(max = 500)
    private String description;

    // Get & Set

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}