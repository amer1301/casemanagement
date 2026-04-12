package com.example.casemanagement.controller;

import com.example.casemanagement.service.CaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.casemanagement.model.CaseStatus;
import com.example.casemanagement.dto.CaseLogDTO;
import com.example.casemanagement.dto.CaseDTO;
import com.example.casemanagement.dto.CreateCaseDTO;
import com.example.casemanagement.dto.UpdateCaseDTO;
import com.example.casemanagement.service.CaseLogService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.example.casemanagement.dto.UpdateCaseStatusDTO;

import jakarta.validation.Valid;

import java.util.List;
@SecurityRequirement(name = "bearerAuth")
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
    public Object getAll(
            @RequestParam(required = false) CaseStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {

        if (status != null) {
            return service.getByStatus(status);
        }

        return service.getAll(page, size, sortBy).getContent();
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

    @PatchMapping("/{id}/status")
    public ResponseEntity<CaseDTO> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCaseStatusDTO dto
    ) {
        return ResponseEntity.ok(service.updateStatus(id, dto.getStatus()));
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
