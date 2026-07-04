package com.saman.ga.jssp.constraints;

import java.util.List;

public record ConstraintEvaluation(
        String constraintName,
        ConstraintLevel level,
        int penalty,
        List<ConstraintViolation> violations,
        String explanation
) {
    public boolean hasViolation() {
        return !violations.isEmpty();
    }

    public static ConstraintEvaluation ok(String name, ConstraintLevel level) {
        return new ConstraintEvaluation(
                name,
                level,
                0,
                List.of(),
                "No violation."
        );
    }
}