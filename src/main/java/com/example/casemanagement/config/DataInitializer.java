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

    @Bean
    CommandLineRunner initManager(UserRepository userRepository,
                                  PasswordEncoder passwordEncoder) {
        return args -> {

            // Säkerställ att endast en manager finns
            if (!userRepository.existsByRole(Role.MANAGER)) {

                User manager = new User(
                        "System Manager",
                        "manager@system.local",
                        passwordEncoder.encode("manager123"),
                        Role.MANAGER
                );

                userRepository.save(manager);

                System.out.println("Manager created at startup");
            }
        };
    }
}