package com.example.casemanagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfiguration av OpenAPI (Swagger) för dokumentation av API:et.
 *
 * Syftet är att:
 * - automatiskt generera dokumentation över endpoints
 * - möjliggöra testning av API via Swagger UI
 * - integrera säkerhetsmekanismer (JWT) i dokumentationen
 *
 * Detta gör det enklare att förstå och interagera med systemet,
 * både under utveckling och vid demonstration.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Definierar OpenAPI-konfigurationen och säkerhetsschemat.
     *
     * Viktigt:
     * - "bearerAuth" kopplas till JWT-baserad autentisering
     * - gör att Swagger UI kräver en token för skyddade endpoints
     *
     * Detta speglar systemets verkliga säkerhetskonfiguration
     * och möjliggör realistisk testning direkt i dokumentationen.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()

                // Anger att API:et använder en global säkerhetsmekanism (JWT)
                // vilket innebär att endpoints förväntar sig en bearer-token
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))

                .components(new Components()
                        .addSecuritySchemes("bearerAuth",

                                // Definierar hur autentisering ska ske i Swagger
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)

                                        // "bearer" används tillsammans med JWT
                                        .scheme("bearer")

                                        // Informativt värde som visar att token är av typen JWT
                                        .bearerFormat("JWT")
                        ));
    }
}