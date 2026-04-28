package com.example.casemanagement.controller;

import com.example.casemanagement.service.RoleRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller för hantering av rollförfrågningar (Role Requests).
 *
 * Denna funktionalitet representerar en separat domänprocess där användare
 * kan begära utökade behörigheter (t.ex. bli ADMIN), och där en MANAGER
 * ansvarar för att godkänna eller avslå dessa förfrågningar.
 *
 * Design:
 * - Tydlig separation mellan användarens handlingar och manager-beslut
 * - Alla affärsregler hanteras i service-lagret
 * - Rollbaserad åtkomstkontroll via @PreAuthorize
 */
@RestController
@RequestMapping("/api/role-requests")
public class RoleRequestController {

    private final RoleRequestService service;

    public RoleRequestController(RoleRequestService service) {
        this.service = service;
    }

    /**
     * Hämtar alla rollförfrågningar.
     *
     * Endast tillgänglig för MANAGER, eftersom denna roll ansvarar
     * för att granska och besluta om förfrågningar.
     */
    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    /**
     * Skapar en ny rollförfrågan för den aktuella användaren.
     *
     * Användarens identitet hämtas i backend (service-lagret),
     * vilket förhindrar manipulation från klienten.
     */
    @PostMapping
    public ResponseEntity<?> create() {
        return ResponseEntity.ok(service.createRoleRequest());
    }

    /**
     * Hämtar den inloggade användarens egna rollförfrågningar.
     *
     * Filtrering sker i service-lagret baserat på autentiserad användare.
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyRequests() {
        return ResponseEntity.ok(service.getMyRequests());
    }

    /**
     * Godkänner en rollförfrågan.
     *
     * Endast MANAGER har rätt att utföra denna operation.
     * Själva uppdateringen av användarens roll hanteras i service-lagret.
     */
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> approve(@PathVariable Long id) {
        return ResponseEntity.ok(service.approveRole(id));
    }

    /**
     * Avslår en rollförfrågan.
     *
     * Detta är en del av samma arbetsflöde som approve och hanteras
     * centralt i service-lagret.
     */
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> reject(@PathVariable Long id) {
        return ResponseEntity.ok(service.rejectRole(id));
    }

    /**
     * Tar bort en rollförfrågan.
     *
     * Implementeras som soft delete i service-lagret, vilket innebär
     * att posten inte tas bort permanent utan markeras som inaktiv.
     *
     * Endast MANAGER har rätt att utföra denna operation.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public void deleteRoleRequest(@PathVariable Long id) {
        service.delete(id);
    }
}