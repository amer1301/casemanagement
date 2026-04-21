package com.example.casemanagement.domain;

import com.example.casemanagement.model.CaseStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CaseStatusTransitionTest {

    @Test
    void shouldAllowValidTransition() {
        assertDoesNotThrow(() ->
                CaseStatusTransition.validate(CaseStatus.SUBMITTED, CaseStatus.APPROVED)
        );
    }

    @Test
    void shouldThrowOnInvalidTransition() {
        assertThrows(Exception.class, () ->
                CaseStatusTransition.validate(CaseStatus.APPROVED, CaseStatus.SUBMITTED)
        );
    }
}