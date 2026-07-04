package com.saman.ga.jssp.model;

import java.util.List;
import java.util.Objects;

/**
 * A job is an ordered route of operations.
 */
public final class Job {
    private final int id;
    private final List<Operation> operations;

    public Job(int id, List<Operation> operations) {
        if (id < 0) {
            throw new IllegalArgumentException("Job id must be non-negative");
        }
        Objects.requireNonNull(operations, "operations");
        if (operations.isEmpty()) {
            throw new IllegalArgumentException("Job must contain at least one operation");
        }
        for (int i = 0; i < operations.size(); i++) {
            Operation operation = operations.get(i);
            if (operation.jobId() != id) {
                throw new IllegalArgumentException("Operation job id does not match job id");
            }
            if (operation.operationIndex() != i) {
                throw new IllegalArgumentException("Operation index must match its route position");
            }
        }
        this.id = id;
        this.operations = List.copyOf(operations);
    }

    public int id() {
        return id;
    }

    public List<Operation> operations() {
        return operations;
    }

    public Operation operation(int operationIndex) {
        return operations.get(operationIndex);
    }

    public int operationCount() {
        return operations.size();
    }
}
