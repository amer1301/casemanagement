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
    void shouldThrowWhenGoingBackward() {
        assertThrows(InvalidTransitionException.class, () ->
                CaseStatusTransition.validate(
                        CaseStatus.APPROVED,
                        CaseStatus.SUBMITTED
                )
        );
    }

    @Test
    void shouldThrowWhenSameStatus() {
        assertThrows(InvalidTransitionException.class, () ->
                CaseStatusTransition.validate(
                        CaseStatus.SUBMITTED,
                        CaseStatus.SUBMITTED
                )
        );
    }
}