package com.example.casemanagement.controller;

import com.example.casemanagement.model.Case;
import com.example.casemanagement.model.CaseStatus;
import com.example.casemanagement.model.Role;
import com.example.casemanagement.model.User;
import com.example.casemanagement.repository.CaseRepository;
import com.example.casemanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private UserRepository userRepository;

    private User admin;
    private Case testCase;

    @BeforeEach
    void setup() {
        caseRepository.deleteAll();
        userRepository.deleteAll();

        admin = new User();
        admin.setEmail("admin@test.com");
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        User owner = new User();
        owner.setEmail("user@test.com");
        owner.setRole(Role.USER);
        userRepository.save(owner);

        testCase = new Case();
        testCase.setTitle("Test case");
        testCase.setDescription("Test desc");
        testCase.setStatus(CaseStatus.SUBMITTED);
        testCase.setUser(owner);
        caseRepository.save(testCase);
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void shouldUpdateStatusSuccessfully() throws Exception {
        String body = """
                {
                "status": "APPROVED"
                }
                """;

        mockMvc.perform(patch("/cases/" + testCase.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status(). isOk());
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void shouldReturnBadRequestForInvalidTransition() throws Exception {
        // Sätt case till APPROVED först
        testCase.setStatus(CaseStatus.APPROVED);
        caseRepository.save(testCase);

        String body = """
                {
                "status": "REJECTED"
                }
                """;

        mockMvc.perform(patch("/cases/" + testCase.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }
}
