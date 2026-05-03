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

    private final CaseService service;
    private final CaseLogService caseLogService;

    public CaseController(CaseService service, CaseLogService caseLogService) {
        this.service = service;
        this.caseLogService = caseLogService;
    }

    /**
     * Hämtar ärenden med stöd för:
     *
     * - Pagination (page, size)
     * - Sortering (sortBy)
     * - Filtrering (status)
     * - Sökning (q)
     *
     * Detta är en "unified endpoint" som ersätter tidigare enklare filtrering.
     *
     * Exempel:
     * /cases?page=0&size=10&status=SUBMITTED&q=akut
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
                service.getAll(page, size, sortBy, direction, status, q, assignedTo)
        );
    }

    /**
     * Skapar ett nytt ärende.
     *
     * - Validering sker via DTO (@Valid)
     * - Prioritet sätts automatiskt i service-lagret
     */
    @PostMapping
    public ApiResponse<CaseDTO> create(@Valid @RequestBody CreateCaseDTO dto) {
        return new ApiResponse<>(service.create(dto));
    }

    /**
     * Hämtar ett specifikt ärende baserat på ID.
     */
    @GetMapping("/{id}")
    public ApiResponse<CaseDTO> getCaseById(@PathVariable Long id) {
        return new ApiResponse<>(service.getCaseById(id));
    }

    /**
     * Tar bort ett ärende.
     *
     * Loggning sker i service-lagret.
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCase(@PathVariable Long id) {
        service.deleteCase(id);
        return new ApiResponse<>("Case deleted");
    }

    /**
     * Uppdaterar titel och beskrivning.
     */
    @PutMapping("/{id}")
    public ApiResponse<CaseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCaseDTO dto) {

        return new ApiResponse<>(service.update(id, dto));
    }

    /**
     * Uppdaterar status på ett ärende.
     *
     * Affärsregler:
     * - Endast ADMIN
     * - Får ej hantera eget ärende
     * - Endast SUBMITTED → APPROVED/REJECTED
     */
    @PatchMapping("/{id}/status")
    public ApiResponse<CaseDTO> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCaseStatusDTO dto
    ) {
        return new ApiResponse<>(
                service.updateStatus(id, dto.getStatus(), dto.getReason())
        );
    }

    /**
     * Hämtar loggar kopplade till ett ärende.
     *
     * Används för historik och spårbarhet.
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
        return new ApiResponse<>(service.getMyCases());
    }

    /**
     * Uppdaterar prioritet.
     *
     * Endast ADMIN har tillgång.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/priority")
    public ApiResponse<String> updatePriority(
            @PathVariable Long id,
            @RequestBody UpdatePriorityRequest request
    ) {
        service.updatePriority(id, request.getPriority());
        return new ApiResponse<>("Priority updated");
    }

    /**
     * Dashboard-data (aggregat).
     */
    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> dashboard() {
        return new ApiResponse<>(service.getDashboardStats());
    }

    /**
     * Tilldelar ärende till inloggad användare.
     */
    @PatchMapping("/{id}/assign")
    public ApiResponse<CaseDTO> assign(@PathVariable Long id) {
        return new ApiResponse<>(service.assignToCurrentUser(id));
    }

    /**
     * Statistik per admin.
     */
    @GetMapping("/dashboard/admins")
    public ApiResponse<List<AdminStatsDTO>> getAdminStats() {
        return new ApiResponse<>(service.getAdminStats());
    }

    /**
     * Hämtar ej tilldelade ärenden.
     */
    @GetMapping("/unassigned")
    public ApiResponse<List<CaseDTO>> getUnassigned() {
        return new ApiResponse<>(service.getUnassignedCases());
    }

    /**
     * Hämtar ärenden tilldelade aktuell användare.
     */
    @GetMapping("/assigned")
    public ApiResponse<List<CaseDTO>> getAssignedToMe() {
        return new ApiResponse<>(service.getMyAssignedCases());
    }

    /**
     * Skapar en överklagan.
     *
     * Påverkar:
     * - status
     * - historik
     * - notifikationer
     */
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