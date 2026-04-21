package com.example.casemanagement.controller;

import com.example.casemanagement.BaseIntegrationTest;
import com.example.casemanagement.dto.CreateCaseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class CaseControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldCreateCase() throws Exception {

        CreateCaseDTO dto = new CreateCaseDTO();
        dto.setTitle("Test case");
        dto.setDescription("Test description");

        mockMvc.perform(post("/cases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Test case"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldGetCases() throws Exception {
        mockMvc.perform(get("/cases"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldBlockAdminEndpoint() throws Exception {
        mockMvc.perform(post("/cases/1/approve-role"))
                .andExpect(status().isForbidden());
    }
}