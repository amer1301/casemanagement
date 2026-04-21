package com.example.casemanagement.controller;

import com.example.casemanagement.dto.ApiResponse;
import com.example.casemanagement.dto.CaseNoteDTO;
import com.example.casemanagement.dto.CreateCaseNoteRequest;
import com.example.casemanagement.service.CaseNoteService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/cases/{caseId}/notes")
public class CaseNoteController {

    private final CaseNoteService service;

    public CaseNoteController(CaseNoteService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<CaseNoteDTO>> getNotes(@PathVariable Long caseId) {
        return new ApiResponse<>(service.getNotes(caseId));
    }

    @PostMapping
    public ApiResponse<CaseNoteDTO> createNote(
            @PathVariable Long caseId,
            @Valid @RequestBody CreateCaseNoteRequest request
    ) {
        request.setCaseId(caseId);

        return new ApiResponse<>(
                service.createNote(request)
        );
    }
}