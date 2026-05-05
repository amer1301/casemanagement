package com.example.casemanagement.service;

import com.example.casemanagement.exception.ForbiddenException;
import com.example.casemanagement.exception.ResourceNotFoundException;
import com.example.casemanagement.model.*;
import com.example.casemanagement.repository.RoleRequestRepository;
import com.example.casemanagement.repository.UserRepository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service för hantering av rollbegäranden.
 *
 * Ansvar:
 * - Skapa rollbegäran (USER → ADMIN)
 * - Godkänna eller avslå begäran (MANAGER)
 * - Hämta begäranden
 * - Soft delete
 *
 * Design:
 * - Implementerar ett enkelt workflow-system
 * - Säkerställer att endast manager får hantera begäranden
 * - Förhindrar duplicerade aktiva begäranden
 */
@Service
public class RoleRequestService {

    private final UserRepository userRepository;
    private final RoleRequestRepository repo;
    private final NotificationService notificationService;

    public RoleRequestService(
            UserRepository userRepository,
            RoleRequestRepository repo,
            NotificationService notificationService
    ) {
        this.userRepository = userRepository;
        this.repo = repo;
        this.notificationService = notificationService;
    }

    /**
     * Hämtar aktuell användare via SecurityContext.
     */
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Säkerställer att användaren är manager.
     */
    private void ensureManager(User user) {
        if (user.getRole() != Role.MANAGER) {
            throw new ForbiddenException("Only manager allowed");
        }
    }

    /**
     * Skapar en ny rollbegäran.
     *
     * Regler:
     * - Endast en aktiv (PENDING) begäran per användare
     */
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

    /**
     * Godkänner en rollbegäran.
     *
     * Effekt:
     * - Användaren uppgraderas till ADMIN
     * - Begäran markeras som APPROVED
     * - Skapar notifiering till användaren
     */
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

        RoleRequest saved = repo.save(r);

        notificationService.createNotification(
                user,
                "Din admin-begäran har blivit godkänd",
                null // ingen case koppling
        );

        return saved;
    }

    /**
     * Avslår en rollbegäran.
     *  * - Begäran markeras som REJECTED
     *  * - Skapar notifiering till användaren
     */
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

        RoleRequest saved = repo.save(r);

        notificationService.createNotification(
                r.getUser(),
                "Din admin-begäran har blivit avslagen",
                null
        );

        return saved;
    }

    /**
     * Hämtar alla aktiva rollbegäranden (endast manager).
     */
    public List<RoleRequest> getAll() {

        User user = getCurrentUser();
        ensureManager(user);

        return repo.findByDeletedFalse();
    }

    /**
     * Hämtar aktuella användarens egna begäranden.
     */
    public List<RoleRequest> getMyRequests() {

        User user = getCurrentUser();

        return repo.findByUserAndDeletedFalse(user);
    }

    /**
     * Soft delete av rollbegäran (endast manager).
     */
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