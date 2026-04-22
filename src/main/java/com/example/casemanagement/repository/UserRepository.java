package com.example.casemanagement.repository;

import com.example.casemanagement.model.Role;
import com.example.casemanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailIgnoreCase(String email);
    List<User> findByRole(Role role);

    boolean existsByRole(Role role);
}