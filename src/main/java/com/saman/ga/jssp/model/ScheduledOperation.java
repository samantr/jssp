package com.saman.ga.jssp.model;

/**
 * A scheduled operation with start/end times.
 */
public record ScheduledOperation(Operation operation, int startTime, int endTime) {
    public ScheduledOperation {
        if (operation == null) {
            throw new IllegalArgumentException("Operation is required");
        }
        if (startTime < 0) {
            throw new IllegalArgumentException("Start time must be non-negative");
        }
        if (endTime <= startTime) {
            throw new IllegalArgumentException("End time must be greater than start time");
        }
        if (endTime - startTime != operation.duration()) {
            throw new IllegalArgumentException("Scheduled duration must equal operation duration");
        }
    }

    public int jobId() {
        return operation.jobId();
    }

    public int operationIndex() {
        return operation.operationIndex();
    }

    public int machineId() {
        return operation.machineId();
    }

    public String label() {
        return operation.label();
    }
}
