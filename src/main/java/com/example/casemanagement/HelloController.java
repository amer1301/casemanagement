package com.example.casemanagement;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/case")
    public Case getCase() {
        return new Case("Test case", "Detta är en test");
    }
}