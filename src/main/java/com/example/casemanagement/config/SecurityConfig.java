package com.example.casemanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Central konfiguration av applikationens säkerhetsmekanismer.
 *
 * Denna klass ansvarar för:
 * - autentisering (JWT)
 * - auktorisering (rollbaserad åtkomst)
 * - säker HTTP-konfiguration
 *
 * Designen bygger på stateless autentisering där ingen server-side session används.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * Konfigurerar säkerhetskedjan för alla inkommande HTTP-anrop.
     *
     * Här definieras:
     * - vilka endpoints som är publika
     * - vilka roller som krävs för åtkomst
     * - hur autentisering ska ske (JWT)
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // KOPPLA CORS KONFIGURATIONEN
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // CSRF skydd inaktiveras eftersom API:et är stateless och använder JWT
                .csrf(csrf -> csrf.disable())

                // Tillåter H2-console att visas i iframe (endast för utvecklingsmiljö)
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

                // Konfigurerar systemet att vara stateless (ingen session lagras)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Definierar auktoriseringsregler för olika endpoints
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Endpoints endast för MANAGER
                        .requestMatchers("/manager/**").hasRole("MANAGER")

                        // Endpoints för ADMIN och MANAGER
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN", "MANAGER")

                        // Specifika domänoperationer som kräver MANAGER
                        .requestMatchers("/cases/*/approve-role").hasRole("MANAGER")
                        .requestMatchers("/cases/*/reject-role").hasRole("MANAGER")

                        // Alla case-endpoints kräver autentisering
                        // ytterligare filtrering sker i service-lagret (defense in depth)
                        .requestMatchers("/cases/**").authenticated()

                        // Standardregel: alla övriga endpoints kräver autentisering
                        .anyRequest().authenticated()
                )

                // Inaktiverar standardmetoder för autentisering (Basic Auth och formulär)
                // eftersom JWT används istället
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable())

                // Lägger till JWT-filter före Spring Securitys standardfilter
                // så att autentisering sker baserat på token innan åtkomst kontrolleras
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * PasswordEncoder används för att hasha lösenord innan lagring.
     *
     * BCrypt används eftersom det är en säker och adaptiv hash-algoritm
     * som skyddar mot brute-force attacker.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Konfiguration av CORS (Cross-Origin Resource Sharing).
     *
     * Tillåter frontend-applikationen (t.ex. React) att kommunicera med backend
     * trots att de körs på olika origin (t.ex. olika portar).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Tillåter anrop från frontend
        config.setAllowedOrigins(java.util.List.of(
                "http://localhost:5173",
                "https://casemanagement-frontend.onrender.com"
        ));

        // Tillåter alla HTTP-metoder (GET, POST, PUT, DELETE, etc.)
        config.setAllowedMethods(java.util.List.of("*"));

        // Tillåter alla headers (inklusive Authorization)
        config.setAllowedHeaders(java.util.List.of("*"));

        // Tillåter cookies/credentials om det behövs
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}