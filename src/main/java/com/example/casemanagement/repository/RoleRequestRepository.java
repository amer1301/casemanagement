package com.example.casemanagement.repository;

import com.example.casemanagement.model.RoleRequest;
import com.example.casemanagement.model.RoleRequestStatus;
import com.example.casemanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRequestRepository extends JpaRepository<RoleRequest, Long> {

    boolean existsByUserAndStatus(User user, RoleRequestStatus status);

    boolean existsByUserAndStatusAndDeletedFalse(User user, RoleRequestStatus status);

    List<RoleRequest> findByUser(User user);

    List<RoleRequest> findByDeletedFalse();

    List<RoleRequest> findByUserAndDeletedFalse(User user);

}