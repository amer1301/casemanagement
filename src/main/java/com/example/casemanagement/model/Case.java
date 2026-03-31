package com.example.casemanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cases")
public class Case {
    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private String description;

    // Tom konstruktor
    public Case() {
    }

    // Konstruktor
    public Case(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // Get & Set
    public Long getId() {
        return id;
    }

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
