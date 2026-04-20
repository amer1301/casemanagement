package com.example.casemanagement.mapper;

import com.example.casemanagement.dto.RegisterRequest;
import com.example.casemanagement.model.Role;
import com.example.casemanagement.model.User;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public User toUser(RegisterRequest request, String encodedPassword) {
        return new User(
                request.getName(),
                request.getEmail(),
                encodedPassword,
                Role.USER
        );
    }
}