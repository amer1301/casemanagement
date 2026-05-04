package com.example.casemanagement.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class SecurityIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = {"USER"})
    void userShouldNotAccessAdminEndpoint() throws Exception {

        mockMvc.perform(put("/cases/1/priority")
                        .contentType("application/json")
                        .content("{\"priority\":1}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void userShouldNotAccessManagerEndpoint() throws Exception {

        mockMvc.perform(get("/reports/monthly"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void userShouldNotApproveRoleRequest() throws Exception {

        mockMvc.perform(post("/api/role-requests/1/approve"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void userShouldAccessCases() throws Exception {

        mockMvc.perform(get("/cases/dashboard"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void managerShouldAccessManagerEndpoint() throws Exception {

        mockMvc.perform(get("/reports/monthly"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldBlockWithoutToken() throws Exception {

        mockMvc.perform(get("/cases")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldBlockWithInvalidToken() throws Exception {

        mockMvc.perform(get("/cases")
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", "Bearer invalid"))
                .andExpect(status().isForbidden());
    }
}