package com.example.casemanagement.domain;

import com.example.casemanagement.exception.InvalidTransitionException;
import com.example.casemanagement.model.CaseStatus;

import java.util.List;
import java.util.Map;

public class CaseStatusTransition {

    private static final Map<CaseStatus, List<CaseStatus>> transitions = Map.of(
            CaseStatus.SUBMITTED, List.of(CaseStatus.APPROVED, CaseStatus.REJECTED),
            CaseStatus.APPROVED, List.of(),
            CaseStatus.REJECTED, List.of()
    );

    public static void validate(CaseStatus from, CaseStatus to) {

        if (from == null || to == null) {
            throw new InvalidTransitionException("Status cannot be null");
        }

        if (!transitions.getOrDefault(from, List.of()).contains(to)) {
            throw new InvalidTransitionException(
                    "Invalid transition: " + from + " -> " + to
            );
        }
    }
}

