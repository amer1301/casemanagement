package com.example.casemanagement.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.casemanagement.config.JwtService;
import com.example.casemanagement.service.CustomUserDetailsService;

import java.io.IOException;

/**
 * JWT-baserat autentiseringsfilter som körs för varje inkommande HTTP-request.
 *
 * Syftet är att:
 * - extrahera JWT-token från Authorization-headern
 * - validera token
 * - identifiera användaren
 * - sätta autentisering i SecurityContext
 *
 * Detta möjliggör stateless autentisering där ingen session lagras på serversidan.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Körs en gång per request och ansvarar för att autentisera användaren
     * baserat på JWT-token.
     *
     * Flöde:
     * 1. Hämta Authorization-header
     * 2. Extrahera token
     * 3. Validera token
     * 4. Hämta användardata
     * 5. Sätt autentisering i SecurityContext
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Hämta Authorization-headern (förväntas innehålla "Bearer <token>")
        String authHeader = request.getHeader("Authorization");

        // Om header saknas eller inte följer Bearer-format → fortsätt utan autentisering
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extrahera själva tokenen (ta bort "Bearer ")
        String token = authHeader.substring(7);

        // Skydd mot ogiltiga eller felaktigt skickade tokens
        if (token == null || token.isBlank() || token.equals("undefined")) {
            filterChain.doFilter(request, response);
            return;
        }

        String email;

        try {
            // Extrahera användarens identitet (email) från token
            email = jwtService.extractEmail(token);
        } catch (Exception e) {
            // Om token inte kan tolkas → ignorera och fortsätt utan autentisering
            filterChain.doFilter(request, response);
            return;
        }

        // Validera token och säkerställ att den inte är manipulerad eller utgången
        if (email != null && jwtService.isTokenValid(token)) {

            // Ladda användarens detaljer från databasen
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // Skapa ett autentiseringsobjekt baserat på användarens roller/behörigheter
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            // Koppla request-specifik metadata (t.ex. IP, session-id)
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Sätt autentisering i SecurityContext → används senare av Spring Security
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // Fortsätt filterkedjan oavsett om autentisering lyckades eller ej
        filterChain.doFilter(request, response);
    }
}