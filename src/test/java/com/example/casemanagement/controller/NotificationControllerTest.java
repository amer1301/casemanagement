package com.example.casemanagement.controller;

import com.example.casemanagement.dto.NotificationDTO;
import com.example.casemanagement.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = NotificationController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = com.example.casemanagement.config.SecurityConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = com.example.casemanagement.config.JwtAuthFilter.class)
        }
)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Test
    @WithMockUser
    void shouldGetNotifications() throws Exception {

        NotificationDTO dto = new NotificationDTO(
                1L,
                "Test message",
                1L,
                false,
                LocalDateTime.now()
        );

        when(notificationService.getMyNotifications())
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("Test message"));
    }

    @Test
    @WithMockUser
    void shouldMarkAllAsRead() throws Exception {

        mockMvc.perform(patch("/notifications/read-all"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void shouldDeleteNotification() throws Exception {

        mockMvc.perform(delete("/notifications/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void shouldGetUnreadCount() throws Exception {

        when(notificationService.getUnreadCount()).thenReturn(5);

        mockMvc.perform(get("/notifications/unread-count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }
}