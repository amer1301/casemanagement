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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class SecurityIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ===================== HELPERS =====================

    private String registerAndLogin(String email, String password) throws Exception {

        RegisterRequest register = new RegisterRequest();
        register.setName("Test User");
        register.setEmail(email);
        register.setPassword(password);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        LoginRequest login = new LoginRequest();
        login.setEmail(email);
        login.setPassword(password);

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        return json.get("data").get("token").asText();
    }

    private String createUserToken() throws Exception {
        String email = "user-" + UUID.randomUUID() + "@test.com";
        return registerAndLogin(email, "password");
    }

    private String loginManager() throws Exception {

        String email = "manager-" + UUID.randomUUID() + "@test.com";

        RegisterRequest register = new RegisterRequest();
        register.setName("Manager");
        register.setEmail(email);
        register.setPassword("password");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        LoginRequest login = new LoginRequest();
        login.setEmail(email);
        login.setPassword("password");

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        return json.get("data").get("token").asText();
    }

    // ===================== SECURITY TESTS =====================

    @Test
    void userShouldNotAccessAdminEndpoint() throws Exception {

        String token = createUserToken();

        mockMvc.perform(put("/cases/1/priority")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"priority\":1}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void userShouldNotAccessManagerEndpoint() throws Exception {

        String token = createUserToken();

        mockMvc.perform(get("/reports/monthly")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void userShouldNotApproveRoleRequest() throws Exception {

        String token = createUserToken();

        mockMvc.perform(post("/api/role-requests/1/approve")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void userShouldAccessCases() throws Exception {

        String token = createUserToken();

        mockMvc.perform(get("/cases")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void managerShouldAccessManagerEndpoint() throws Exception {

        String token = loginManager();

        mockMvc.perform(get("/reports/monthly")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldBlockWithoutToken() throws Exception {

        mockMvc.perform(get("/cases"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldBlockWithInvalidToken() throws Exception {

        mockMvc.perform(get("/cases")
                        .header("Authorization", "Bearer invalid"))
                .andExpect(status().isForbidden());
    }
}