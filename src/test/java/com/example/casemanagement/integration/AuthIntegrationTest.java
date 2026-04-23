package com.example.casemanagement.integration;

import com.example.casemanagement.dto.LoginRequest;
import com.example.casemanagement.dto.RegisterRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class AuthIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ===================== FULL FLOW =====================
    @Test
    void shouldRegisterLoginAndAccessProtectedEndpoint() throws Exception {

        String email = "test-" + UUID.randomUUID() + "@test.com";

        // ===== REGISTER =====
        RegisterRequest register = new RegisterRequest();
        register.setName("Test User");
        register.setEmail(email);
        register.setPassword("password");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"));

        // ===== LOGIN =====
        LoginRequest login = new LoginRequest();
        login.setEmail(email);
        login.setPassword("password");

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        String token = json.get("data").get("token").asText();

        assertNotNull(token);
        assertFalse(token.isBlank());

        // ===== ACCESS PROTECTED =====
        mockMvc.perform(get("/cases")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    // ===================== NO TOKEN =====================
    @Test
    void shouldBlockWithoutToken() throws Exception {

        mockMvc.perform(get("/cases"))
                .andExpect(status().isForbidden()); // 👈 viktigt (inte 401)
    }

    // ===================== INVALID TOKEN =====================
    @Test
    void shouldBlockWithInvalidToken() throws Exception {

        mockMvc.perform(get("/cases")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isForbidden()); // 👈 viktigt
    }

    // ===================== WRONG LOGIN =====================
    @Test
    void shouldFailLoginWithWrongCredentials() throws Exception {

        LoginRequest login = new LoginRequest();
        login.setEmail("fake@test.com");
        login.setPassword("wrong");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized()); // 👈 detta stämmer
    }
}