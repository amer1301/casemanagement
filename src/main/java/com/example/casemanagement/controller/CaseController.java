package com.example.casemanagement.controller;

import com.example.casemanagement.dto.*;
import com.example.casemanagement.model.CaseStatus;
import com.example.casemanagement.service.CaseLogService;
import com.example.casemanagement.service.CaseService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller för hantering av ärenden (cases).
 *
 * Ansvar:
 * - Exponerar REST-endpoints
 * - Tar emot request-parametrar
 * - Returnerar standardiserade svar via ApiResponse
 *
 * Designprinciper:
 * - Ingen affärslogik i controller
 * - Delegation till service-lagret
 * - Stöd för pagination, filtrering och sökning
 *
 * Säkerhet:
 * - Kräver JWT (bearerAuth)
 * - Rollbaserad access via @PreAuthorize
 */
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/cases")
public class CaseController {

    private final CaseService caseService;
    private final CaseLogService caseLogService;

    public CaseController(CaseService caseService, CaseLogService caseLogService) {
        this.caseService = caseService;
        this.caseLogService = caseLogService;
    }

    /**
     * Hämtar ärenden med stöd för:
     * - Pagination (page, size)
     * - Sortering (sortBy)
     * - Filtrering (status)
     * - Sökning (q)
     */
    @GetMapping
    public ApiResponse<?> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) CaseStatus status,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long assignedTo
    ) {
        return new ApiResponse<>(
                caseService.getAll(page, size, sortBy, direction, status, q, assignedTo)
        );
    }

    /**
     * Skapar ett nytt ärende.
     */
    @PostMapping
    public ApiResponse<CaseDTO> create(@Valid @RequestBody CreateCaseDTO dto) {
        return new ApiResponse<>(caseService.create(dto));
    }

    /**
     * Hämtar ett specifikt ärende baserat på ID.
     */
    @GetMapping("/{id}")
    public ApiResponse<CaseDTO> getCaseById(@PathVariable Long id) {
        return new ApiResponse<>(caseService.getCaseById(id));
    }

    /**
     * Tar bort ett ärende.
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCase(@PathVariable Long id) {
        caseService.deleteCase(id);
        return new ApiResponse<>("Case deleted");
    }

    /**
     * Uppdaterar titel och beskrivning.
     */
    @PutMapping("/{id}")
    public ApiResponse<CaseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCaseDTO dto
    ) {
        return new ApiResponse<>(caseService.update(id, dto));
    }

    /**
     * Uppdaterar status på ett ärende.
     */
    @PatchMapping("/{id}/status")
    public ApiResponse<CaseDTO> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCaseStatusDTO dto
    ) {
        return new ApiResponse<>(
                caseService.updateStatus(id, dto.getStatus(), dto.getReason())
        );
    }

    /**
     * Hämtar loggar kopplade till ett ärende.
     */
    @GetMapping("/{id}/logs")
    public ApiResponse<List<CaseLogDTO>> getLogs(@PathVariable Long id) {
        return new ApiResponse<>(caseLogService.getLogs(id));
    }

    /**
     * Hämtar den inloggade användarens egna ärenden.
     */
    @GetMapping("/my")
    public ApiResponse<List<CaseDTO>> getMyCase() {
        return new ApiResponse<>(caseService.getMyCases());
    }

    /**
     * Uppdaterar prioritet (endast ADMIN).
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/priority")
    public ApiResponse<String> updatePriority(
            @PathVariable Long id,
            @RequestBody UpdatePriorityRequest request
    ) {
        caseService.updatePriority(id, request.getPriority());
        return new ApiResponse<>("Priority updated");
    }

    /**
     * Dashboard-data.
     */
    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> dashboard() {
        return new ApiResponse<>(caseService.getDashboardStats());
    }

    /**
     * Tilldelar ärende till inloggad användare.
     */
    @PatchMapping("/{id}/assign")
    public ApiResponse<CaseDTO> assign(@PathVariable Long id) {
        return new ApiResponse<>(caseService.assignToCurrentUser(id));
    }

    /**
     * Statistik per admin.
     */
    @GetMapping("/dashboard/admins")
    public ApiResponse<List<AdminStatsDTO>> getAdminStats() {
        return new ApiResponse<>(caseService.getAdminStats());
    }

    /**
     * Hämtar ej tilldelade ärenden.
     */
    @GetMapping("/unassigned")
    public ApiResponse<List<CaseDTO>> getUnassigned() {
        return new ApiResponse<>(caseService.getUnassignedCases());
    }

    /**
     * Hämtar ärenden tilldelade aktuell användare.
     */
    @GetMapping("/assigned")
    public ApiResponse<List<CaseDTO>> getAssignedToMe() {
        return new ApiResponse<>(caseService.getMyAssignedCases());
    }

    /**
     * Skapar en överklagan.
     */
    @PostMapping("/{id}/appeal")
    public ApiResponse<CaseDTO> appealCase(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        return new ApiResponse<>(
                caseService.appealCase(id, body.get("reason"))
        );
    }

    /**
     * Tar bort en anteckning.
     *
     * - Endast ADMIN/MANAGER (kontrolleras i service)
     * - Används från frontend för att radera notes
     */
    @DeleteMapping("/notes/{noteId}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long noteId) {
        caseService.deleteNote(noteId);
        return ResponseEntity.noContent().build();
    }
}