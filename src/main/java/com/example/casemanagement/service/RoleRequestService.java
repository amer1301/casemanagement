package com.example.casemanagement.service;

import com.example.casemanagement.exception.ForbiddenException;
import com.example.casemanagement.exception.ResourceNotFoundException;
import com.example.casemanagement.model.*;
import com.example.casemanagement.repository.RoleRequestRepository;
import com.example.casemanagement.repository.UserRepository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleRequestService {

    private final UserRepository userRepository;
    private final RoleRequestRepository repo;

    public RoleRequestService(
            UserRepository userRepository,
            RoleRequestRepository repo
    ) {
        this.userRepository = userRepository;
        this.repo = repo;
    }

    // =========================
    // HELPER
    // =========================
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void ensureManager(User user) {
        if (user.getRole() != Role.MANAGER) {
            throw new ForbiddenException("Only manager allowed");
        }
    }

    // =========================
    // CREATE
    // =========================
    public RoleRequest createRoleRequest() {

        User user = getCurrentUser();

        boolean exists = repo.existsByUserAndStatusAndDeletedFalse(
                user,
                RoleRequestStatus.PENDING
        );

        if (exists) {
            throw new ForbiddenException("Du har redan en aktiv admin-begäran");
        }

        RoleRequest request = new RoleRequest(user);

        return repo.save(request);
    }

    // =========================
    // APPROVE
    // =========================
    public RoleRequest approveRole(Long id) {

        User manager = getCurrentUser();
        ensureManager(manager);

        RoleRequest r = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));

        if (r.isDeleted()) {
            throw new ForbiddenException("Request is deleted");
        }

        if (r.getStatus() != RoleRequestStatus.PENDING) {
            throw new ForbiddenException("Already handled");
        }

        User user = r.getUser();
        user.setRole(Role.ADMIN);
        userRepository.save(user);

        r.setStatus(RoleRequestStatus.APPROVED);

        return repo.save(r);
    }

    // =========================
    // REJECT
    // =========================
    public RoleRequest rejectRole(Long id) {

        User manager = getCurrentUser();
        ensureManager(manager);

        RoleRequest r = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));

        if (r.isDeleted()) {
            throw new ForbiddenException("Request is deleted");
        }

        if (r.getStatus() != RoleRequestStatus.PENDING) {
            throw new ForbiddenException("Already handled");
        }

        r.setStatus(RoleRequestStatus.REJECTED);

        return repo.save(r);
    }

    // =========================
    // GET ALL (MANAGER)
    // =========================
    public List<RoleRequest> getAll() {

        User user = getCurrentUser();
        ensureManager(user);

        return repo.findByDeletedFalse();
    }

    // =========================
    // GET MY REQUESTS
    // =========================
    public List<RoleRequest> getMyRequests() {

        User user = getCurrentUser();

        return repo.findByUserAndDeletedFalse(user);
    }

    // =========================
    // DELETE (SOFT DELETE)
    // =========================
    public void delete(Long id) {

        User manager = getCurrentUser();
        ensureManager(manager);

        RoleRequest request = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        if (request.isDeleted()) {
            throw new ForbiddenException("Already deleted");
        }

        request.setDeleted(true);
        repo.save(request);
    }
}