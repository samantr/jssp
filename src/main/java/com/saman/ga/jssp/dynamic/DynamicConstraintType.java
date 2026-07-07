package com.saman.ga.jssp.dynamic;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum DynamicConstraintType {

    MACHINE_TIME_LIMIT,
    JOB_FINISH_DEADLINE;

    @JsonCreator
    public static DynamicConstraintType fromJson(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Dynamic constraint type is required");
        }

        try {
            return DynamicConstraintType.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unsupported dynamic constraint type: " + value, ex);
        }
    }

    @JsonValue
    public String toJson() {
        return name();
    }
}