package com.example.casemanagement.controller;

import com.example.casemanagement.model.Case;
import com.example.casemanagement.service.CaseService;
import org.springframework.web.bind.annotation.*;
import com.example.casemanagement.model.CaseStatus;
import com.example.casemanagement.dto.CaseLogDTO;
import com.example.casemanagement.dto.CaseDTO;
import com.example.casemanagement.dto.CreateCaseDTO;
import com.example.casemanagement.dto.UpdateCaseDTO;
import com.example.casemanagement.service.CaseLogService;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.util.List;
@RestController
@RequestMapping("/cases")
public class CaseController {
    private final CaseService service;
    private final CaseLogService caseLogService;

    public CaseController(CaseService service, CaseLogService caseLogService) {

        this.service = service;
        this.caseLogService = caseLogService;
    }

    @GetMapping
    public Page<CaseDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

                return service.getAll(page, size);
    }

    @PostMapping
    public CaseDTO create(@Valid @RequestBody CreateCaseDTO dto) {

        return service.create(dto);
    }

    @GetMapping("/{id}")
    public CaseDTO getCaseById(@PathVariable Long id) {
        return service.getCaseById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteCase(@PathVariable Long id) {
        service.deleteCase(id);
    }

    @PutMapping("/{id}")
    public CaseDTO update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCaseDTO dto) {
        return service.update(id, dto);
    }

    @PutMapping("/{id}/status")
    public Case updateStatus(@PathVariable Long id, @RequestParam CaseStatus status) {
        return service.updateStatus(id, status);
    }

    @GetMapping("/{id}/logs")
    public List<CaseLogDTO> getLogs(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId) {

        return caseLogService.getLogs(id, userId);
    }

    @GetMapping("/my")
    public List<CaseDTO> getMyCase() {
        return service.getMyCases();
    }
}
