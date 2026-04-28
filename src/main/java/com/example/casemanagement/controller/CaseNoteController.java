package com.example.casemanagement.controller;

import com.example.casemanagement.dto.ApiResponse;
import com.example.casemanagement.dto.CaseNoteDTO;
import com.example.casemanagement.dto.CreateCaseNoteRequest;
import com.example.casemanagement.service.CaseNoteService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller för hantering av anteckningar kopplade till ett ärende.
 *
 * Noter är en underresurs till Case, vilket återspeglas i URL-strukturen:
 * /cases/{caseId}/notes
 *
 * Controllern ansvarar enbart för att hantera HTTP-anrop och delegerar
 * all affärslogik till service-lagret.
 */
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/cases/{caseId}/notes")
public class CaseNoteController {

    private final CaseNoteService service;

    public CaseNoteController(CaseNoteService service) {
        this.service = service;
    }

    /**
     * Hämtar alla anteckningar för ett specifikt ärende.
     *
     * caseId används för att filtrera noter i service-lagret.
     */
    @GetMapping
    public ApiResponse<List<CaseNoteDTO>> getNotes(@PathVariable Long caseId) {
        return new ApiResponse<>(service.getNotes(caseId));
    }

    /**
     * Skapar en ny anteckning kopplad till ett ärende.
     *
     * caseId sätts explicit i request-objektet för att säkerställa
     * att kopplingen mellan note och case inte kan manipuleras från klienten.
     *
     * Validering sker via DTO (@Valid) och logik hanteras i service-lagret.
     */
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