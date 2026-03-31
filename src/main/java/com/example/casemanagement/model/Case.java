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
}
