package com.example.casemanagement.repository;

import com.example.casemanagement.model.Role;
import com.example.casemanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

/**
 * Repository för User-entiteten.
 *
 * Ansvarar för databasanrop kopplade till användare.
 * Används främst i autentisering, auktorisering och
 * användarhantering.
 *
 * Design:
 * - Bygger på Spring Data JPA för automatisk query-generering
 * - Används av säkerhetslagret (CustomUserDetailsService)
 * - Innehåller metoder för både lookup och validering
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Hämtar användare baserat på e-postadress.
     *
     * Används vid login/autentisering.
     */
    Optional<User> findByEmail(String email);

    /**
     * Hämtar användare utan hänsyn till versaler/gemener.
     *
     * Viktigt för användarvänlighet och robust autentisering.
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Hämtar alla användare med en viss roll.
     *
     * Används exempelvis för att hitta alla admins eller managers.
     */
    List<User> findByRole(Role role);

    /**
     * Kontrollerar om det finns en användare med en viss roll.
     *
     * Används t.ex. vid systemstart för att säkerställa
     * att en manager finns (DataInitializer).
     */
    boolean existsByRole(Role role);
}