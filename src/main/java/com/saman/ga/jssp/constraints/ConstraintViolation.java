package com.saman.ga.jssp.constraints;

public record ConstraintViolation(
        String message,
        int penalty
) {
}