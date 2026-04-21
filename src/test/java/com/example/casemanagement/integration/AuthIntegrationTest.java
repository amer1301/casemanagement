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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class AuthIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRegisterLoginAndAccessProtectedEndpoint() throws Exception {

        // ===== 1. REGISTER =====
        RegisterRequest register = new RegisterRequest();
        register.setName("Test User");
        register.setEmail("test@test.com");
        register.setPassword("password");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        // ===== 2. LOGIN =====
        LoginRequest login = new LoginRequest();
        login.setEmail("test@test.com");
        login.setPassword("password");

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // ===== 3. EXTRACT TOKEN =====
        JsonNode json = objectMapper.readTree(loginResponse);

        // ApiResponse<AuthResponse> → data.token
        String token = json
                .get("data")
                .get("token")
                .asText();

        // sanity check
        org.junit.jupiter.api.Assertions.assertNotNull(token);

        // ===== 4. CALL PROTECTED ENDPOINT =====
        mockMvc.perform(get("/cases")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}