package com.example.casemanagement.controller;

import com.example.casemanagement.service.CaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.casemanagement.model.CaseStatus;
import com.example.casemanagement.dto.CaseLogDTO;
import com.example.casemanagement.dto.CaseDTO;
import com.example.casemanagement.dto.CreateCaseDTO;
import com.example.casemanagement.dto.UpdateCaseDTO;
import com.example.casemanagement.dto.AdminStatsDTO;
import com.example.casemanagement.service.CaseLogService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.example.casemanagement.dto.UpdateCaseStatusDTO;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

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
        return ResponseEntity.ok(
                service.updateStatus(id, dto.getStatus(), dto.getReason())
        );
    }

    @GetMapping("/{id}/logs")
    public List<CaseLogDTO> getLogs(@PathVariable Long id) {
        return caseLogService.getLogs(id);
    }

    @GetMapping("/my")
    public List<CaseDTO> getMyCase() {
        return service.getMyCases();
    }

    @PostMapping("/request-admin")
    public CaseDTO requestAdmin() {
        return service.requestAdmin();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/priority")
    public void updatePriority(@PathVariable Long id, @RequestBody Integer priority) {
        service.updatePriority(id, priority);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/{id}/approve-role")
    public CaseDTO approveRole(@PathVariable Long id) {
        return service.approveRole(id);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/{id}/reject-role")
    public CaseDTO rejectRole(@PathVariable Long id) {
        return service.rejectRole(id);
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        return service.getDashboardStats();
    }

    @PatchMapping("/{id}/assign")
    public CaseDTO assign(@PathVariable Long id) {
        return service.assignToCurrentUser(id);
    }

    @GetMapping("/dashboard/admins")
    public List<AdminStatsDTO> getAdminStats() {
        return service.getAdminStats();
    }

    @GetMapping("/unassigned")
    public List<CaseDTO> getUnassigned() {
        return service.getUnassignedCases();
    }

    @GetMapping("/assigned")
    public List<CaseDTO> getAssignedToMe() {
        return service.getMyAssignedCases();
    }

    @PostMapping("/{id}/appeal")
    public ResponseEntity<CaseDTO> appealCase(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        return ResponseEntity.ok(
                service.appealCase(id, body.get("reason"))
        );
    }
}
