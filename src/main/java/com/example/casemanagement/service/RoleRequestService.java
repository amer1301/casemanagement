package com.example.casemanagement.service;

import com.example.casemanagement.exception.ForbiddenException;
import com.example.casemanagement.exception.ResourceNotFoundException;
import com.example.casemanagement.mapper.CaseMapper;
import com.example.casemanagement.model.*;
import com.example.casemanagement.repository.CaseRepository;
import com.example.casemanagement.repository.UserRepository;

import org.springframework.stereotype.Service;

@Service
public class RoleRequestService {

    private final CaseRepository repo;
    private final UserRepository userRepository;
    private final CaseLogService caseLogService;
    private final CaseMapper mapper;

    public RoleRequestService(CaseRepository repo,
                              UserRepository userRepository,
                              CaseLogService caseLogService,
                              CaseMapper mapper) {
        this.repo = repo;
        this.userRepository = userRepository;
        this.caseLogService = caseLogService;
        this.mapper = mapper;
    }

    public Case createRoleRequest(User user) {

        boolean exists = repo.existsByUserAndTypeAndStatus(
                user,
                "ROLE_REQUEST",
                CaseStatus.SUBMITTED
        );

        if (exists) {
            throw new ForbiddenException("Du har redan en aktiv admin-begäran");
        }

        Case c = mapper.toRoleRequestCase(user);

        Case saved = repo.save(c);

        caseLogService.logAction(saved, user, "ROLE_REQUEST_CREATED");

        return saved;
    }

    public Case approveRole(Long id, User manager) {

        ensureManager(manager);

        Case c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        if (!Case.TYPE_ROLE_REQUEST.equals(c.getType())) {
            throw new ForbiddenException("Wrong type");
        }

        if (c.getStatus() != CaseStatus.SUBMITTED) {
            throw new ForbiddenException("Case already handled");
        }

        User user = c.getUser();
        user.setRole(Role.ADMIN);
        userRepository.save(user);

        c.setAssignedTo(manager);
        c.setStatus(CaseStatus.APPROVED);

        Case saved = repo.save(c);

        caseLogService.logAction(
                saved,
                manager,
                "ROLE_REQUEST_APPROVED"
        );

        return saved;
    }

    public Case rejectRole(Long id, User manager) {

        ensureManager(manager);

        Case c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        if (!"ROLE_REQUEST".equals(c.getType())) {
            throw new ForbiddenException("Wrong type");
        }

        if (c.getStatus() != CaseStatus.SUBMITTED) {
            throw new ForbiddenException("Case already handled");
        }

        c.setStatus(CaseStatus.REJECTED);

        return repo.save(c);
    }

    private void ensureManager(User user) {
        if (user.getRole() != Role.MANAGER) {
            throw new ForbiddenException("Only manager allowed");
        }
    }
}
