package com.example.casemanagement.domain;

import com.example.casemanagement.exception.InvalidTransitionException;
import com.example.casemanagement.model.CaseStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CaseStatusTransitionTest {
    @Test
    void shouldAllowSubmittedToApproved() {
        assertDoesNotThrow(() ->
                CaseStatusTransition.validate(
                        CaseStatus.SUBMITTED,
                        CaseStatus.APPROVED
                )
        );
    }

    @Test
    void shouldAllowSubmittedToRejected() {
        assertDoesNotThrow(() ->
                CaseStatusTransition.validate(
                        CaseStatus.SUBMITTED,
                        CaseStatus.REJECTED
                )
        );
    }

    @Test
    void shouldNotAllowApprovedToRejected() {
        assertThrows(InvalidTransitionException.class, () ->
                CaseStatusTransition.validate(
                        CaseStatus.APPROVED,
                        CaseStatus.REJECTED
                )
        );
    }

    @Test void shouldNotAllowRejectedToApproved() {
        assertThrows(InvalidTransitionException.class, () ->
                CaseStatusTransition.validate(
                        CaseStatus.REJECTED,
                        CaseStatus.APPROVED
                )
        );
    }

    @Test
    void shouldThrowIfNullStatus() {
        assertThrows(InvalidTransitionException.class, () ->
                CaseStatusTransition.validate(null, CaseStatus.APPROVED)
        );
    }
}
