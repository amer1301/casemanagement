package com.example.casemanagement.service;

import com.example.casemanagement.config.JwtService;
import com.example.casemanagement.dto.LoginRequest;
import com.example.casemanagement.dto.RegisterRequest;
import com.example.casemanagement.mapper.AuthMapper;
import com.example.casemanagement.model.User;
import com.example.casemanagement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public User register(RegisterRequest request) {

        // 1. Encode password
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 2. Map DTO -> Entity
        User user = authMapper.toUser(request, encodedPassword);

        // 3. Save
        return userRepository.save(user);
    }

    public String login(LoginRequest request) {

        // 1. Hämta user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Verifiera lösenord
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // 3. Generera token
        return jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );
    }
}