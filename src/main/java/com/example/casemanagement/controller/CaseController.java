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
 * Denna klass ansvarar för att exponera API-endpoints för klienten
 * och delegerar all affärslogik till service-lagret.
 *
 * Designprinciper:
 * - Tunn controller (ingen affärslogik)
 * - REST-baserad struktur
 * - Standardiserade svar via ApiResponse
 *
 * Säkerhet:
 * - Kräver JWT (bearerAuth)
 * - Viss åtkomst styrs via @PreAuthorize
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
     * Hämtar ärenden.
     *
     * Stödjer:
     * - filtrering på status
     * - paginering
     * - sortering
     *
     * Valet att hantera filter här (status != null) är en enkel routing-logik,
     * medan själva datalogiken ligger i service-lagret.
     */
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

    /**
     * Skapar ett nytt ärende.
     *
     * Validering sker via DTO (@Valid) och affärslogik hanteras i service-lagret.
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
     * Eventuell loggning och validering hanteras i service-lagret.
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCase(@PathVariable Long id) {
        service.deleteCase(id);
        return new ApiResponse<>("Case deleted");
    }

    /**
     * Uppdaterar grundläggande information för ett ärende.
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
     * Detta är en domänspecifik operation (inte ren CRUD),
     * där regler och tillåtna övergångar hanteras i service-lagret.
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
     * Används för spårbarhet och audit logging.
     */
    @GetMapping("/{id}/logs")
    public ApiResponse<List<CaseLogDTO>> getLogs(@PathVariable Long id) {
        return new ApiResponse<>(caseLogService.getLogs(id));
    }

    /**
     * Hämtar endast den inloggade användarens ärenden.
     *
     * Filtrering baseras på användarens identitet i backend.
     */
    @GetMapping("/my")
    public ApiResponse<List<CaseDTO>> getMyCase() {
        return new ApiResponse<>(service.getMyCases());
    }

    /**
     * Uppdaterar prioritet på ett ärende.
     *
     * Endast tillåtet för ADMIN, vilket hanteras via @PreAuthorize.
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
     * Hämtar statistik för dashboard.
     *
     * Returnerar aggregerad data snarare än enskilda entiteter.
     */
    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> dashboard() {
        return new ApiResponse<>(service.getDashboardStats());
    }

    /**
     * Tilldelar ett ärende till den inloggade användaren.
     *
     * Regler (t.ex. att inte tilldela eget ärende) hanteras i service-lagret.
     */
    @PatchMapping("/{id}/assign")
    public ApiResponse<CaseDTO> assign(@PathVariable Long id) {
        return new ApiResponse<>(service.assignToCurrentUser(id));
    }

    /**
     * Hämtar statistik per administratör.
     */
    @GetMapping("/dashboard/admins")
    public ApiResponse<List<AdminStatsDTO>> getAdminStats() {
        return new ApiResponse<>(service.getAdminStats());
    }

    /**
     * Hämtar alla ärenden som ännu inte är tilldelade.
     */
    @GetMapping("/unassigned")
    public ApiResponse<List<CaseDTO>> getUnassigned() {
        return new ApiResponse<>(service.getUnassignedCases());
    }

    /**
     * Hämtar ärenden som är tilldelade till den aktuella användaren.
     */
    @GetMapping("/assigned")
    public ApiResponse<List<CaseDTO>> getAssignedToMe() {
        return new ApiResponse<>(service.getMyAssignedCases());
    }

    /**
     * Skapar en överklagan på ett ärende.
     *
     * Detta är en avancerad domänoperation som påverkar:
     * - status
     * - historik (loggar)
     * - notifikationer
     *
     * All logik hanteras i service-lagret.
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