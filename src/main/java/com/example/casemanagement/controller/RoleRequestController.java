package com.example.casemanagement.controller;

import com.example.casemanagement.model.User;
import com.example.casemanagement.service.RoleRequestService;
import com.example.casemanagement.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role-requests")
public class RoleRequestController {

    private final RoleRequestService service;
    private final UserRepository userRepository;

    public RoleRequestController(RoleRequestService service,
                                 UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(Authentication auth) {
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public ResponseEntity<?> getAll(Authentication auth) {
        User user = getCurrentUser(auth);
        return ResponseEntity.ok(service.getAll(user));
    }

    @PostMapping
    public ResponseEntity<?> create(Authentication auth) {
        User user = getCurrentUser(auth);
        return ResponseEntity.ok(service.createRoleRequest(user));
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyRequests(Authentication auth) {
        User user = getCurrentUser(auth);
        return ResponseEntity.ok(service.getMyRequests(user));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id, Authentication auth) {
        User manager = getCurrentUser(auth);
        return ResponseEntity.ok(service.approveRole(id, manager));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id, Authentication auth) {
        User manager = getCurrentUser(auth);
        return ResponseEntity.ok(service.rejectRole(id, manager));
    }
}