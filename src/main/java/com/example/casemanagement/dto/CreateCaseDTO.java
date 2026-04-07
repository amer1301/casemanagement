package com.example.casemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateCaseDTO {

    @NotBlank(message = "Titeln får inte vara tom")
        @Size(min = 3, max = 100)
    private String title;

    @NotBlank(message = "Beskrivning får inte vara tom")
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
