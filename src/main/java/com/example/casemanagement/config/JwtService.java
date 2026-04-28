package com.example.casemanagement.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * Service för hantering av JWT (JSON Web Tokens).
 *
 * Ansvar:
 * - Generera tokens vid autentisering
 * - Extrahera information från tokens
 * - Validera tokens
 *
 * Designen bygger på stateless autentisering, där all nödvändig
 * användarinformation lagras i token istället för i server-sessioner.
 */
@Service
public class JwtService {

    /**
     * Hemlig nyckel för signering av JWT.
     *
     * Hämtas från applikationens konfiguration (application.properties)
     * för att möjliggöra säker hantering och enkel ändring mellan miljöer.
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Tokenns giltighetstid (1 timme).
     *
     * Begränsad livslängd minskar risken vid eventuell token-kompromettering.
     */
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour

    /**
     * Genererar signeringsnyckel baserat på den hemliga strängen.
     *
     * Nyckeln används för att både signera och verifiera JWT,
     * vilket säkerställer att token inte kan manipuleras.
     */
    private Key getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Skapar en JWT-token för en autentiserad användare.
     *
     * Innehåller:
     * - subject (email) → identifierar användaren
     * - role → används för auktorisering
     * - issuedAt → när token skapades
     * - expiration → när token upphör att gälla
     *
     * Token signeras med HS256 för att säkerställa integritet.
     */
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extraherar användarens email (subject) från token.
     *
     * Används som primär identifierare i autentiseringsflödet.
     */
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extraherar användarens roll från token.
     *
     * Rollen används senare i säkerhetslagret för åtkomstkontroll.
     */
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    /**
     * Validerar om token är giltig.
     *
     * Returnerar false om:
     * - token är ogiltig
     * - token har gått ut
     * - parsing misslyckas (t.ex. manipulerad token)
     *
     * Detta förhindrar att ogiltiga tokens orsakar systemfel.
     */
    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Kontrollerar om token har gått ut baserat på expiration-timestamp.
     */
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    /**
     * Parsar token och extraherar alla claims.
     *
     * Verifierar samtidigt signaturen med hjälp av den hemliga nyckeln,
     * vilket säkerställer att token inte har manipulerats.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}