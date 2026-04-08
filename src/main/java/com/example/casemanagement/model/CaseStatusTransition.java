package com.example.casemanagement.model;

import com.example.casemanagement.exception.InvalidTransitionException;

import java.util.List;
import java.util.Map;

public class CaseStatusTransition {

    private static final Map<CaseStatus, List<CaseStatus>> transitions = Map.of(
            CaseStatus.SUBMITTED, List.of(CaseStatus.APPROVED, CaseStatus.REJECTED),
            CaseStatus.APPROVED, List.of(),
            CaseStatus.REJECTED, List.of()
    );

    public static void validate(CaseStatus from, CaseStatus to) {
        if (!transitions.getOrDefault(from, List.of()).contains(to)) {
            throw new InvalidTransitionException(
                    "Invalid transition: " + from + " -> " + to
            );
        }
    }
}
