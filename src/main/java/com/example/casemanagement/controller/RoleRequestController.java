package com.example.casemanagement.controller;

import com.example.casemanagement.service.RoleRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role-requests")
public class RoleRequestController {

    private final RoleRequestService service;

    public RoleRequestController(RoleRequestService service) {
        this.service = service;
    }

    // =========================
    // GET ALL (MANAGER)
    // =========================
    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // =========================
    // CREATE
    // =========================
    @PostMapping
    public ResponseEntity<?> create() {
        return ResponseEntity.ok(service.createRoleRequest());
    }

    // =========================
    // GET MY
    // =========================
    @GetMapping("/my")
    public ResponseEntity<?> getMyRequests() {
        return ResponseEntity.ok(service.getMyRequests());
    }

    // =========================
    // APPROVE
    // =========================
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> approve(@PathVariable Long id) {
        return ResponseEntity.ok(service.approveRole(id));
    }

    // =========================
    // REJECT
    // =========================
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> reject(@PathVariable Long id) {
        return ResponseEntity.ok(service.rejectRole(id));
    }

    // =========================
    // DELETE (SOFT DELETE)
    // =========================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public void deleteRoleRequest(@PathVariable Long id) {
        service.delete(id);
    }
}