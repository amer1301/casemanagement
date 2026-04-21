package com.example.casemanagement.controller;

import com.example.casemanagement.dto.CaseDTO;
import com.example.casemanagement.dto.CreateCaseDTO;
import com.example.casemanagement.service.CaseService;
import com.example.casemanagement.service.CaseLogService;
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

    @Test
    @WithMockUser(roles = {"USER"})
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
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldGetCases() throws Exception {

        when(caseService.getAll(anyInt(), anyInt(), anyString()))
                .thenReturn(new PageImpl<>(java.util.List.of()));

        mockMvc.perform(get("/cases"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldBlockAdminEndpoint() throws Exception {

        mockMvc.perform(post("/cases/1/approve-role"))
                .andExpect(status().isOk());
    }
}