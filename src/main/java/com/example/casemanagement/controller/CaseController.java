package com.example.casemanagement.controller;

import com.example.casemanagement.model.Case;
import com.example.casemanagement.service.CaseService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
@RestController
@RequestMapping("/cases")
public class CaseController {
    private final CaseService service;

    public CaseController(CaseService service) {
        this.service = service;
    }

    @GetMapping
    public List<Case> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Case create(@Valid @RequestBody Case c) {
        return service.create(c);
    }

    @GetMapping("/{id}")
    public Case getCaseById(@PathVariable Long id) {
        return service.getCaseById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteCase(@PathVariable Long id) {
        service.deleteCase(id);
    }

    @PutMapping("/{id}")
    public Case updatedCase(@PathVariable Long id, @Valid @RequestBody Case c) {
        return service.update(id, c);
    }
}
