package com.example.casemanagement.controller;

import com.example.casemanagement.dto.*;
import com.example.casemanagement.model.CaseStatus;
import com.example.casemanagement.service.CaseLogService;
import com.example.casemanagement.service.CaseService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    // GET ALL / FILTER
    @GetMapping
    public ApiResponse<?> getAll(
            @RequestParam(required = false) CaseStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {

        if (status != null) {
            return new ApiResponse<>(service.getByStatus(status));
        }

        return new ApiResponse<>(service.getAll(page, size, sortBy).getContent());
    }

    // CREATE
    @PostMapping
    public ApiResponse<CaseDTO> create(@Valid @RequestBody CreateCaseDTO dto) {
        return new ApiResponse<>(service.create(dto));
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ApiResponse<CaseDTO> getCaseById(@PathVariable Long id) {
        return new ApiResponse<>(service.getCaseById(id));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCase(@PathVariable Long id) {
        service.deleteCase(id);
        return new ApiResponse<>("Case deleted");
    }

    // UPDATE
    @PutMapping("/{id}")
    public ApiResponse<CaseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCaseDTO dto) {

        return new ApiResponse<>(service.update(id, dto));
    }

    // UPDATE STATUS
    @PatchMapping("/{id}/status")
    public ApiResponse<CaseDTO> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCaseStatusDTO dto
    ) {
        return new ApiResponse<>(
                service.updateStatus(id, dto.getStatus(), dto.getReason())
        );
    }

    // LOGS
    @GetMapping("/{id}/logs")
    public ApiResponse<List<CaseLogDTO>> getLogs(@PathVariable Long id) {
        return new ApiResponse<>(caseLogService.getLogs(id));
    }

    // MY CASES
    @GetMapping("/my")
    public ApiResponse<List<CaseDTO>> getMyCase() {
        return new ApiResponse<>(service.getMyCases());
    }


    // UPDATE PRIORITY
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/priority")
    public ApiResponse<String> updatePriority(
            @PathVariable Long id,
            @RequestBody UpdatePriorityRequest request
    ) {
        service.updatePriority(id, request.getPriority());
        return new ApiResponse<>("Priority updated");
    }


    // DASHBOARD
    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> dashboard() {
        return new ApiResponse<>(service.getDashboardStats());
    }

    // ASSIGN CASE
    @PatchMapping("/{id}/assign")
    public ApiResponse<CaseDTO> assign(@PathVariable Long id) {
        return new ApiResponse<>(service.assignToCurrentUser(id));
    }

    // ADMIN STATS
    @GetMapping("/dashboard/admins")
    public ApiResponse<List<AdminStatsDTO>> getAdminStats() {
        return new ApiResponse<>(service.getAdminStats());
    }

    // UNASSIGNED
    @GetMapping("/unassigned")
    public ApiResponse<List<CaseDTO>> getUnassigned() {
        return new ApiResponse<>(service.getUnassignedCases());
    }

    // ASSIGNED TO ME
    @GetMapping("/assigned")
    public ApiResponse<List<CaseDTO>> getAssignedToMe() {
        return new ApiResponse<>(service.getMyAssignedCases());
    }

    // APPEAL
    @PostMapping("/{id}/appeal")
    public ApiResponse<CaseDTO> appealCase(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        return new ApiResponse<>(
                service.appealCase(id, body.get("reason"))
        );
    }
}