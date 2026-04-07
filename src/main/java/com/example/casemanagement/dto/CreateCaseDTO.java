package com.example.casemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateCaseDTO {

    @NotBlank(message = "Titeln får inte vara tom")
        @Size(min = 3, max = 100, message = "Titeln måste vara mellan 3 och 100 tecken")
    private String title;

    @NotBlank(message = "Beskrivning får inte vara tom")
    @Size(max = 500, message = "Beskrivningen får max vara 500 tecken")
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
