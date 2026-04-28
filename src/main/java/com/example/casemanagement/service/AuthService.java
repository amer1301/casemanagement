package com.example.casemanagement.service;

import com.example.casemanagement.config.JwtService;
import com.example.casemanagement.dto.AuthResponse;
import com.example.casemanagement.dto.LoginRequest;
import com.example.casemanagement.dto.RegisterRequest;
import com.example.casemanagement.mapper.AuthMapper;
import com.example.casemanagement.model.User;
import com.example.casemanagement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.BadCredentialsException;

/**
 * Service som hanterar autentisering och registrering av användare.
 *
 * Ansvar:
 * - Registrera nya användare
 * - Autentisera användare vid login
 * - Generera JWT-token
 * - Bygga responsobjekt för klienten
 *
 * Design:
 * - Innehåller affärslogik (inte controller/repository)
 * - Använder mapper för konvertering mellan DTO och Entity
 * - Integrerar med Spring Security (PasswordEncoder)
 * - Använder JWT för stateless autentisering
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthMapper authMapper;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthMapper authMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authMapper = authMapper;
    }

    /**
     * Registrerar en ny användare.
     *
     * Flöde:
     * 1. Kryptera lösenord
     * 2. Mappa DTO → Entity
     * 3. Spara i databasen
     */
    public User register(RegisterRequest request) {

        // 1. Kryptera lösenord för säker lagring
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 2. Konvertera DTO till User-entitet
        User user = authMapper.toUser(request, encodedPassword);

        // 3. Spara användaren i databasen
        return userRepository.save(user);
    }

    /**
     * Autentiserar en användare och returnerar en JWT-token.
     *
     * Flöde:
     * 1. Hämta användare via email (case-insensitive)
     * 2. Verifiera lösenord
     * 3. Generera JWT-token
     */
    public String login(LoginRequest request) {

        // Trimma email för att undvika fel pga whitespace
        String email = request.getEmail().trim();

        // Hämta användare (case-insensitive)
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        // Verifiera lösenord
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        // Generera JWT-token med email och roll
        return jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );
    }

    /**
     * Bygger ett AuthResponse-objekt som returneras till klienten.
     *
     * Innehåller:
     * - token
     * - email
     * - namn
     * - roll
     */
    public AuthResponse buildAuthResponse(String email, String token) {

        // Hämta användare från databasen
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Bygg och returnera response DTO
        return new AuthResponse(
                token,
                user.getEmail(),
                user.getName(),
                user.getRole().name()
        );
    }
}