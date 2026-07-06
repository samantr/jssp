package com.saman.ga.jssp.constraints;

import com.saman.ga.jssp.model.ScheduledOperation;

/**
 * One concrete violation produced by a constraint.
 *
 * The message is human-readable.
 * The optional operation fields make explanations more structured.
 */
public record ConstraintViolation(
        String message,
        int penalty,
        String operationLabel,
        Integer jobId,
        Integer operationIndex,
        Integer machineId,
        Integer startTime,
        Integer endTime
) {

    public ConstraintViolation {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Violation message is required");
        }

        if (penalty < 0) {
            throw new IllegalArgumentException("Violation penalty cannot be negative");
        }
    }

    public ConstraintViolation(String message, int penalty) {
        this(
                message,
                penalty,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public static ConstraintViolation forOperation(
            String message,
            int penalty,
            ScheduledOperation operation
    ) {
        if (operation == null) {
            return new ConstraintViolation(message, penalty);
        }

        return new ConstraintViolation(
                message,
                penalty,
                operation.label(),
                operation.jobId(),
                operation.operationIndex(),
                operation.machineId(),
                operation.startTime(),
                operation.endTime()
        );
    }

    public boolean hasOperationReference() {
        return operationLabel != null;
    }
}