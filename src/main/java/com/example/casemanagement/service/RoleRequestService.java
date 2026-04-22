package com.example.casemanagement.service;

import com.example.casemanagement.exception.ForbiddenException;
import com.example.casemanagement.exception.ResourceNotFoundException;
import com.example.casemanagement.mapper.CaseMapper;
import java.util.List;
import com.example.casemanagement.model.*;
import com.example.casemanagement.repository.CaseRepository;
import com.example.casemanagement.repository.RoleRequestRepository;
import com.example.casemanagement.repository.UserRepository;

import org.springframework.stereotype.Service;

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

    public RoleRequest createRoleRequest(User user) {

        boolean exists = repo.existsByUserAndStatus(
                user,
                RoleRequestStatus.PENDING
        );

        if (exists) {
            throw new ForbiddenException("Du har redan en aktiv admin-begäran");
        }

        RoleRequest request = new RoleRequest(user);

        return repo.save(request);
    }

    public RoleRequest approveRole(Long id, User manager) {

        ensureManager(manager);

        RoleRequest r = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));

        if (r.getStatus() != RoleRequestStatus.PENDING) {
            throw new ForbiddenException("Already handled");
        }

        User user = r.getUser();
        user.setRole(Role.ADMIN);
        userRepository.save(user);

        r.setStatus(RoleRequestStatus.APPROVED);

        return repo.save(r);
    }

    public RoleRequest rejectRole(Long id, User manager) {

        ensureManager(manager);

        RoleRequest r = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));

        if (r.getStatus() != RoleRequestStatus.PENDING) {
            throw new ForbiddenException("Already handled");
        }

        r.setStatus(RoleRequestStatus.REJECTED);

        return repo.save(r);
    }

    private void ensureManager(User user) {
        if (user.getRole() != Role.MANAGER) {
            throw new ForbiddenException("Only manager allowed");
        }
    }

    public List<RoleRequest> getAll(User user) {

        if (user.getRole() != Role.MANAGER) {
            throw new ForbiddenException("Only manager allowed");
        }

        return repo.findAll();
    }

    public List<RoleRequest> getMyRequests(User user) {
        return repo.findByUser(user);
    }
}
