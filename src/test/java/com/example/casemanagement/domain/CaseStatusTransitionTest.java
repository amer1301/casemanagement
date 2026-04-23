package com.example.casemanagement.domain;

import com.example.casemanagement.exception.InvalidTransitionException;
import com.example.casemanagement.model.CaseStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CaseStatusTransitionTest {

    // ===================== VALID =====================

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

    // ===================== INVALID =====================

    @Test
    void shouldThrowWhenGoingBackward() {

        InvalidTransitionException ex = assertThrows(
                InvalidTransitionException.class,
                () -> CaseStatusTransition.validate(
                        CaseStatus.APPROVED,
                        CaseStatus.SUBMITTED
                )
        );

        assertTrue(ex.getMessage().contains("APPROVED -> SUBMITTED"));
    }

    @Test
    void shouldThrowWhenSameStatus() {

        InvalidTransitionException ex = assertThrows(
                InvalidTransitionException.class,
                () -> CaseStatusTransition.validate(
                        CaseStatus.SUBMITTED,
                        CaseStatus.SUBMITTED
                )
        );

        assertTrue(ex.getMessage().contains("SUBMITTED -> SUBMITTED"));
    }

    // ===================== NULL =====================

    @Test
    void shouldThrowWhenFromIsNull() {

        InvalidTransitionException ex = assertThrows(
                InvalidTransitionException.class,
                () -> CaseStatusTransition.validate(null, CaseStatus.SUBMITTED)
        );

        assertEquals("Status cannot be null", ex.getMessage());
    }

    @Test
    void shouldThrowWhenToIsNull() {

        InvalidTransitionException ex = assertThrows(
                InvalidTransitionException.class,
                () -> CaseStatusTransition.validate(CaseStatus.SUBMITTED, null)
        );

        assertEquals("Status cannot be null", ex.getMessage());
    }

    // ===================== TERMINAL STATES =====================

    @Test
    void shouldNotAllowAnyTransitionFromApproved() {

        assertThrows(InvalidTransitionException.class, () ->
                CaseStatusTransition.validate(
                        CaseStatus.APPROVED,
                        CaseStatus.REJECTED
                )
        );
    }

    @Test
    void shouldNotAllowAnyTransitionFromRejected() {

        assertThrows(InvalidTransitionException.class, () ->
                CaseStatusTransition.validate(
                        CaseStatus.REJECTED,
                        CaseStatus.APPROVED
                )
        );
    }
}