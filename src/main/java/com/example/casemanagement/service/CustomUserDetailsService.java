package com.example.casemanagement.service;

import com.example.casemanagement.model.User;
import com.example.casemanagement.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

/**
 * Anpassad implementation av Spring Securitys UserDetailsService.
 *
 * Ansvar:
 * - Hämta användare från databasen vid autentisering
 * - Konvertera User-entitet till Spring Security UserDetails
 *
 * Design:
 * - Integrerar applikationens User-modell med Spring Security
 * - Används automatiskt av autentiseringsprocessen
 * - Säkerställer att roller mappas korrekt till authorities
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Laddar användare baserat på e-postadress.
     *
     * Denna metod anropas automatiskt av Spring Security vid login.
     *
     * Flöde:
     * 1. Hämta användare från databasen
     * 2. Validera att användaren har en roll
     * 3. Konvertera till UserDetails med korrekt authority-format
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // Hämta användare
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Säkerställ att användaren har en roll
        if (user.getRole() == null) {
            throw new UsernameNotFoundException("User has no role assigned");
        }

        // Konvertera roll till Spring Security-format (ROLE_*)
        String role = "ROLE_" + user.getRole().name();

        // Returnera UserDetails-objekt som används av Spring Security
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(role))
        );
    }
}