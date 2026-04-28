package com.example.casemanagement.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Entitet som representerar en användare i systemet.
 *
 * Integrerar med Spring Security genom att implementera UserDetails,
 * vilket gör att användaren kan användas direkt i autentisering och
 * auktorisering.
 *
 * Design:
 * - Mappas till databastabellen "users"
 * - Innehåller både domäninformation och säkerhetsrelaterade egenskaper
 * - Kopplar användarroller till Spring Security via GrantedAuthority
 *
 * Säkerhet:
 * - Lösenord lagras krypterat (hanteras i service-lagret)
 * - Roll används för att styra åtkomst till endpoints
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {

    /** Primärnyckel */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Användarens namn */
    private String name;

    /** Unik e-postadress (används som username) */
    @Column(unique = true)
    private String email;

    /** Krypterat lösenord */
    private String password;

    /** Användarens roll */
    @Enumerated(EnumType.STRING)
    private Role role;

    public User() {}

    public User(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    /**
     * Sätter användarens roll.
     *
     * Affärsregel:
     * - Rollen MANAGER får inte ändras
     *   (skyddar systemets högsta behörighetsnivå)
     */
    public void setRole(Role role) {
        if (this.role == Role.MANAGER) {
            throw new RuntimeException("Manager role cannot be changed");
        }
        this.role = role;
    }

    public Role getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    /**
     * Returnerar användarens behörigheter (roles) till Spring Security.
     *
     * Prefixet "ROLE_" krävs av Spring Security för att matcha
     * @PreAuthorize och säkerhetsregler.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                (GrantedAuthority) () -> "ROLE_" + role.name()
        );
    }

    /** Returnerar krypterat lösenord */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returnerar användarnamn (email används istället för username)
     */
    @Override
    public String getUsername() {
        return email;
    }

    // Nedan metoder används av Spring Security
    // och returnerar true för att indikera att kontot är aktivt

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}