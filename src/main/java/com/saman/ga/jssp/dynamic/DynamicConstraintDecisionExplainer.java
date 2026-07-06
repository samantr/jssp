package com.saman.ga.jssp.dynamic;

import com.saman.ga.jssp.dynamic.DynamicConstraint;
import com.saman.ga.jssp.model.JsspInstance;

import java.util.Map;
import java.util.Objects;

public final class DynamicConstraintDecisionExplainer {

    public String explainAccepted(DynamicConstraint constraint, JsspInstance instance) {
        Objects.requireNonNull(constraint, "constraint");
        Objects.requireNonNull(instance, "instance");

        StringBuilder explanation = new StringBuilder();

        explanation.append("Dynamic constraint accepted: ")
                .append(constraint.name())
                .append(System.lineSeparator());

        explanation.append("Reason: JSON parsed successfully, constraint type is supported, ")
                .append("required parameters exist, and IDs are valid for instance '")
                .append(instance.name())
                .append("'.")
                .append(System.lineSeparator());

        explanation.append("Type: ")
                .append(constraint.type())
                .append(System.lineSeparator());

        explanation.append("Level: ")
                .append(constraint.level())
                .append(System.lineSeparator());

        explanation.append("Weight: ")
                .append(constraint.weight())
                .append(System.lineSeparator());

        explanation.append("Instance jobs: ")
                .append(instance.numberOfJobs())
                .append(System.lineSeparator());

        explanation.append("Instance machines: ")
                .append(instance.numberOfMachines())
                .append(System.lineSeparator());

        explanation.append("Parameters:")
                .append(System.lineSeparator());

        for (Map.Entry<String, Integer> entry : constraint.parameters().entrySet()) {
            explanation.append("- ")
                    .append(entry.getKey())
                    .append(" = ")
                    .append(entry.getValue())
                    .append(System.lineSeparator());
        }

        return explanation.toString();
    }

    public String explainRejected(String sourceDescription, RuntimeException exception) {
        Objects.requireNonNull(sourceDescription, "sourceDescription");
        Objects.requireNonNull(exception, "exception");

        StringBuilder explanation = new StringBuilder();

        explanation.append("Dynamic constraint rejected: ")
                .append(sourceDescription)
                .append(System.lineSeparator());

        explanation.append("Reason: ")
                .append(rootMessage(exception))
                .append(System.lineSeparator());

        explanation.append("The constraint was not added to the GA fitness calculation.");

        return explanation.toString();
    }

    private String rootMessage(Throwable throwable) {
        Throwable current = throwable;

        while (current.getCause() != null) {
            current = current.getCause();
        }

        if (current.getMessage() == null || current.getMessage().isBlank()) {
            return current.getClass().getSimpleName();
        }

        return current.getMessage();
    }
}