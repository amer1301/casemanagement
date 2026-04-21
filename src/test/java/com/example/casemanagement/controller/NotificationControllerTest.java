package com.example.casemanagement.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void shouldGetNotifications() throws Exception {
        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void shouldMarkAllAsRead() throws Exception {
        mockMvc.perform(patch("/notifications/read-all"))
                .andExpect(status().isOk());
    }
}