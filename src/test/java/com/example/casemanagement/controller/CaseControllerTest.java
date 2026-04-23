package com.example.casemanagement.controller;

import com.example.casemanagement.dto.*;
import com.example.casemanagement.model.CaseStatus;
import com.example.casemanagement.service.CaseLogService;
import com.example.casemanagement.service.CaseService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import org.springframework.data.domain.PageImpl;

import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = CaseController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = com.example.casemanagement.config.SecurityConfig.class
                ),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = com.example.casemanagement.config.JwtAuthFilter.class
                )
        }
)
@AutoConfigureMockMvc(addFilters = false)
class CaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CaseService caseService;

    @MockBean
    private CaseLogService caseLogService;

    // ================= CREATE =================

    @Test
    @WithMockUser
    void shouldCreateCase() throws Exception {

        CreateCaseDTO dto = new CreateCaseDTO();
        dto.setTitle("Test case");
        dto.setDescription("Test description");

        CaseDTO response = new CaseDTO();
        response.setTitle("Test case");

        when(caseService.create(any())).thenReturn(response);

        mockMvc.perform(post("/cases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Test case"));
    }

    // ================= GET ALL =================

    @Test
    @WithMockUser
    void shouldGetAllCases() throws Exception {

        when(caseService.getAll(anyInt(), anyInt(), anyString()))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/cases"))
                .andExpect(status().isOk());
    }

    // ================= GET BY ID =================

    @Test
    @WithMockUser
    void shouldGetCaseById() throws Exception {

        CaseDTO dto = new CaseDTO();
        dto.setTitle("Test");

        when(caseService.getCaseById(1L)).thenReturn(dto);

        mockMvc.perform(get("/cases/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Test"));
    }

    // ================= UPDATE =================

    @Test
    @WithMockUser
    void shouldUpdateCase() throws Exception {

        UpdateCaseDTO dto = new UpdateCaseDTO();
        dto.setTitle("Updated title");
        dto.setDescription("Updated description");

        CaseDTO response = new CaseDTO();
        response.setTitle("Updated title");

        when(caseService.update(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/cases/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Updated title"));
    }

    // ================= DELETE =================

    @Test
    @WithMockUser
    void shouldDeleteCase() throws Exception {

        mockMvc.perform(delete("/cases/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Case deleted"));
    }

    // ================= STATUS =================

    @Test
    @WithMockUser
    void shouldUpdateStatus() throws Exception {

        UpdateCaseStatusDTO dto = new UpdateCaseStatusDTO();
        dto.setStatus(CaseStatus.APPROVED);

        when(caseService.updateStatus(eq(1L), any(), any()))
                .thenReturn(new CaseDTO());

        mockMvc.perform(patch("/cases/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    // ================= LOGS =================

    @Test
    @WithMockUser
    void shouldGetLogs() throws Exception {

        when(caseLogService.getLogs(1L)).thenReturn(List.of());

        mockMvc.perform(get("/cases/1/logs"))
                .andExpect(status().isOk());
    }

    // ================= MY CASES =================

    @Test
    @WithMockUser
    void shouldGetMyCases() throws Exception {

        when(caseService.getMyCases()).thenReturn(List.of());

        mockMvc.perform(get("/cases/my"))
                .andExpect(status().isOk());
    }

    // ================= PRIORITY =================

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminToUpdatePriority() throws Exception {

        UpdatePriorityRequest request = new UpdatePriorityRequest();
        request.setPriority(3);

        mockMvc.perform(put("/cases/1/priority")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldAllowUserWhenSecurityDisabled() throws Exception {

        UpdatePriorityRequest request = new UpdatePriorityRequest();
        request.setPriority(3);

        mockMvc.perform(put("/cases/1/priority")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // ================= DASHBOARD =================

    @Test
    @WithMockUser
    void shouldGetDashboard() throws Exception {

        when(caseService.getDashboardStats()).thenReturn(Map.of());

        mockMvc.perform(get("/cases/dashboard"))
                .andExpect(status().isOk());
    }

    // ================= ASSIGN =================

    @Test
    @WithMockUser
    void shouldAssignCase() throws Exception {

        when(caseService.assignToCurrentUser(1L)).thenReturn(new CaseDTO());

        mockMvc.perform(patch("/cases/1/assign"))
                .andExpect(status().isOk());
    }

    // ================= ADMIN STATS =================

    @Test
    @WithMockUser
    void shouldGetAdminStats() throws Exception {

        when(caseService.getAdminStats()).thenReturn(List.of());

        mockMvc.perform(get("/cases/dashboard/admins"))
                .andExpect(status().isOk());
    }

    // ================= FILTER =================

    @Test
    @WithMockUser
    void shouldGetCasesByStatus() throws Exception {

        when(caseService.getByStatus(any())).thenReturn(List.of());

        mockMvc.perform(get("/cases")
                        .param("status", "SUBMITTED"))
                .andExpect(status().isOk());
    }

    // ================= APPEAL =================

    @Test
    @WithMockUser
    void shouldAppealCase() throws Exception {

        when(caseService.appealCase(eq(1L), any()))
                .thenReturn(new CaseDTO());

        mockMvc.perform(post("/cases/1/appeal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"test\"}"))
                .andExpect(status().isOk());
    }
}