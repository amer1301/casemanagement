package com.example.casemanagement.controller;

import com.example.casemanagement.dto.CaseNoteDTO;
import com.example.casemanagement.service.CaseNoteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cases/{caseId}/notes")
public class CaseNoteController {

    private final CaseNoteService service;

    public CaseNoteController(CaseNoteService service) {
        this.service = service;
    }

    // Hämta anteckningar
    @GetMapping
    public List<CaseNoteDTO> getNotes(@PathVariable Long caseId) {
        return service.getNotes(caseId);
    }

    // Skapa anteckning
    @PostMapping
    public CaseNoteDTO createNote(
            @PathVariable Long caseId,
            @RequestBody Map<String, String> body
    ) {
        return service.createNote(caseId, body.get("text"));
    }
}
