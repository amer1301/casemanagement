package com.example.casemanagement.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.casemanagement.model.Role;
import com.example.casemanagement.model.User;
import com.example.casemanagement.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String email, String password) {
        User user = new User(
                email,
                passwordEncoder.encode(password),
                Role.USER
        );

        return userRepository.save(user);
    }
}
