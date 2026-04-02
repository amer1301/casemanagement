package com.example.casemanagement.controller;

import com.example.casemanagement.model.Case;
import com.example.casemanagement.service.CaseService;
import org.springframework.web.bind.annotation.*;
import com.example.casemanagement.model.CaseStatus;

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

    @PutMapping("/{id}/status")
    public Case updateStatus(@PathVariable Long id, @RequestParam CaseStatus status) {
        return service.updateStatus(id, status);
    }

    @GetMapping("/my")
    public List<Case> getMyCase() {
        return service.getMyCases();
    }
}
