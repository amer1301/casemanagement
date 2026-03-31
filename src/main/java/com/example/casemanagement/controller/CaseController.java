package com.example.casemanagement.controller;

import com.example.casemanagement.model.Case;
import com.example.casemanagement.service.CaseService;
import org.springframework.web.bind.annotation.*;

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
    public Case create(@RequestBody Case c) {
        return service.create(c);
    }
}
