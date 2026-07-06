package com.saman.ga.jssp.dynamic;

import com.saman.ga.jssp.constraints.ConstraintLevel;

import java.util.Map;
import java.util.Objects;

public record DynamicConstraint(
        String name,
        DynamicConstraintType type,
        ConstraintLevel level,
        Integer weight,
        Map<String, Integer> parameters
) {

    public DynamicConstraint {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Dynamic constraint name is required");
        }

        Objects.requireNonNull(type, "Dynamic constraint type is required");
        Objects.requireNonNull(level, "Dynamic constraint level is required");
        Objects.requireNonNull(weight, "Dynamic constraint weight is required");

        if (weight <= 0) {
            throw new IllegalArgumentException("Dynamic constraint weight must be positive");
        }

        Objects.requireNonNull(parameters, "Dynamic constraint parameters are required");
        parameters = Map.copyOf(parameters);
    }

    public int requiredIntParameter(String parameterName) {
        Integer value = parameters.get(parameterName);

        if (value == null) {
            throw new IllegalArgumentException(
                    "Missing required parameter '" + parameterName + "' for constraint '" + name + "'"
            );
        }

        return value;
    }
}