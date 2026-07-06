package com.saman.ga.jssp.explanation;

import com.saman.ga.jssp.constraints.ConstraintEvaluation;
import com.saman.ga.jssp.constraints.ConstraintViolation;

import java.util.List;

public record ConstraintExplanationReport(
        int scheduleMakespan,
        int totalPenalty,
        int violatedConstraintCount,
        int totalViolationCount,
        List<ConstraintEvaluation> evaluations
) {

    public ConstraintExplanationReport {
        if (scheduleMakespan < 0) {
            throw new IllegalArgumentException("Schedule makespan cannot be negative");
        }

        if (totalPenalty < 0) {
            throw new IllegalArgumentException("Total penalty cannot be negative");
        }

        if (violatedConstraintCount < 0) {
            throw new IllegalArgumentException("Violated constraint count cannot be negative");
        }

        if (totalViolationCount < 0) {
            throw new IllegalArgumentException("Total violation count cannot be negative");
        }

        evaluations = List.copyOf(evaluations);
    }

    public boolean hasConstraints() {
        return !evaluations.isEmpty();
    }

    public boolean hasViolations() {
        return totalViolationCount > 0;
    }

    public String toText() {
        StringBuilder report = new StringBuilder();

        report.append("Constraint explanation report")
                .append(System.lineSeparator());
        report.append("Schedule makespan: ")
                .append(scheduleMakespan)
                .append(System.lineSeparator());
        report.append("Total penalty: ")
                .append(totalPenalty)
                .append(System.lineSeparator());
        report.append("Violated constraints: ")
                .append(violatedConstraintCount)
                .append(System.lineSeparator());
        report.append("Total violations: ")
                .append(totalViolationCount)
                .append(System.lineSeparator());

        if (evaluations.isEmpty()) {
            report.append("No dynamic constraints were provided.");
            return report.toString();
        }

        for (ConstraintEvaluation evaluation : evaluations) {
            report.append(System.lineSeparator());
            report.append("----------------------------------------")
                    .append(System.lineSeparator());
            report.append("Constraint: ")
                    .append(evaluation.constraintName())
                    .append(System.lineSeparator());
            report.append("Level: ")
                    .append(evaluation.level())
                    .append(System.lineSeparator());
            report.append("Penalty: ")
                    .append(evaluation.penalty())
                    .append(System.lineSeparator());
            report.append("Status: ")
                    .append(evaluation.hasViolation() ? "VIOLATED" : "OK")
                    .append(System.lineSeparator());

            if (!evaluation.hasViolation()) {
                report.append(evaluation.explanation())
                        .append(System.lineSeparator());
                continue;
            }

            for (ConstraintViolation violation : evaluation.violations()) {
                appendViolation(report, violation);
            }
        }

        return report.toString();
    }

    private void appendViolation(StringBuilder report, ConstraintViolation violation) {
        report.append("Violation: ")
                .append(violation.message())
                .append(System.lineSeparator());

        if (violation.hasOperationReference()) {
            report.append("Operation: ")
                    .append(violation.operationLabel())
                    .append(", Job: ")
                    .append(violation.jobId())
                    .append(", Operation index: ")
                    .append(violation.operationIndex())
                    .append(", Machine: ")
                    .append(violation.machineId())
                    .append(", Start: ")
                    .append(violation.startTime())
                    .append(", End: ")
                    .append(violation.endTime())
                    .append(System.lineSeparator());
        }

        report.append("Penalty added: ")
                .append(violation.penalty())
                .append(System.lineSeparator());
    }
}