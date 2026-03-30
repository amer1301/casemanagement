package com.example.casemanagement;

public class Case {

    private String title;
    private String description;

    public Case() {}

    public Case(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}