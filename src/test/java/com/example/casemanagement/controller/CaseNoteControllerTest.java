package com.example.casemanagement.controller;

import com.example.casemanagement.dto.CaseNoteDTO;
import com.example.casemanagement.dto.CreateCaseNoteRequest;
import com.example.casemanagement.service.CaseNoteService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = CaseNoteController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = com.example.casemanagement.config.SecurityConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = com.example.casemanagement.config.JwtAuthFilter.class)
        }
)
@AutoConfigureMockMvc(addFilters = false)
class CaseNoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CaseNoteService caseNoteService;

    @Test
    void shouldCreateNote() throws Exception {

        CreateCaseNoteRequest request = new CreateCaseNoteRequest();
        request.setText("Test note");

        CaseNoteDTO response = new CaseNoteDTO(
                1L,
                "Test note",
                "test@test.com",
                LocalDateTime.now()
        );

        when(caseNoteService.createNote(any()))
                .thenReturn(response);

        mockMvc.perform(post("/cases/1/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.text").value("Test note"));
    }

    @Test
    void shouldGetNotes() throws Exception {

        CaseNoteDTO note = new CaseNoteDTO(
                1L,
                "Note 1",
                "test@test.com",
                LocalDateTime.now()
        );

        when(caseNoteService.getNotes(1L))
                .thenReturn(List.of(note));

        mockMvc.perform(get("/cases/1/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].text").value("Note 1"));
    }
}