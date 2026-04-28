package com.example.casemanagement.config;

import com.example.casemanagement.model.Role;
import com.example.casemanagement.model.User;
import com.example.casemanagement.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    /**
     * Initialiserar systemet med en standard Manager-användare vid uppstart.
     *
     * Syftet är att säkerställa att administrativa funktioner alltid är åtkomliga,
     * även i en ny eller tom databasmiljö.
     *
     * Implementationen kontrollerar om en användare med rollen MANAGER redan finns
     * för att undvika duplicering. Detta gör initialiseringen idempotent, vilket
     * innebär att den kan köras flera gånger utan att skapa inkonsistent data.
     */
    @Bean
    CommandLineRunner initManager(UserRepository userRepository,
                                  PasswordEncoder passwordEncoder) {
        return args -> {

            // Kontrollera om en manager redan existerar för att undvika flera administrativa konton
            if (!userRepository.existsByRole(Role.MANAGER)) {

                User manager = new User(
                        "System Manager",
                        "manager@system.local",
                        passwordEncoder.encode("manager123"),
                        Role.MANAGER
                );

                userRepository.save(manager);

                // Loggning för att tydliggöra att initialisering har skett (används främst i utvecklingsmiljö)
                System.out.println("Manager created at startup");
            }
        };
    }
}