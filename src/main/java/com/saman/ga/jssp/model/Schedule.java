package com.saman.ga.jssp.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * A decoded schedule. The decoder constructs schedules that respect job precedence and machine capacity.
 */
public final class Schedule {
    private final List<ScheduledOperation> operations;

    public Schedule(List<ScheduledOperation> operations) {
        Objects.requireNonNull(operations, "operations");
        this.operations = List.copyOf(operations);
    }

    public List<ScheduledOperation> operations() {
        return operations;
    }

    public int makespan() {
        return operations.stream()
                .mapToInt(ScheduledOperation::endTime)
                .max()
                .orElse(0);
    }

    public List<ScheduledOperation> operationsForJob(int jobId) {
        return operations.stream()
                .filter(operation -> operation.jobId() == jobId)
                .sorted(Comparator.comparingInt(ScheduledOperation::operationIndex))
                .toList();
    }

    public List<ScheduledOperation> operationsForMachine(int machineId) {
        return operations.stream()
                .filter(operation -> operation.machineId() == machineId)
                .sorted(Comparator.comparingInt(ScheduledOperation::startTime)
                        .thenComparingInt(ScheduledOperation::endTime))
                .toList();
    }

    public ScheduledOperation find(int jobId, int operationIndex) {
        return operations.stream()
                .filter(operation -> operation.jobId() == jobId && operation.operationIndex() == operationIndex)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Scheduled operation not found: J" + jobId + "-O" + operationIndex));
    }

    public boolean respectsPrecedence(JsspInstance instance) {
        for (int jobId = 0; jobId < instance.numberOfJobs(); jobId++) {
            List<ScheduledOperation> jobOperations = operationsForJob(jobId);
            if (jobOperations.size() != instance.job(jobId).operationCount()) {
                return false;
            }
            for (int i = 1; i < jobOperations.size(); i++) {
                ScheduledOperation previous = jobOperations.get(i - 1);
                ScheduledOperation current = jobOperations.get(i);
                if (previous.endTime() > current.startTime()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean hasNoMachineConflicts(JsspInstance instance) {
        for (int machineId = 0; machineId < instance.numberOfMachines(); machineId++) {
            List<ScheduledOperation> machineOperations = operationsForMachine(machineId);
            for (int i = 1; i < machineOperations.size(); i++) {
                ScheduledOperation previous = machineOperations.get(i - 1);
                ScheduledOperation current = machineOperations.get(i);
                if (previous.endTime() > current.startTime()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean schedulesEveryOperationExactlyOnce(JsspInstance instance) {
        if (operations.size() != instance.totalOperations()) {
            return false;
        }
        boolean[][] seen = new boolean[instance.numberOfJobs()][instance.operationsPerJob()];
        for (ScheduledOperation scheduledOperation : operations) {
            int jobId = scheduledOperation.jobId();
            int operationIndex = scheduledOperation.operationIndex();
            if (jobId < 0 || jobId >= instance.numberOfJobs()) {
                return false;
            }
            if (operationIndex < 0 || operationIndex >= instance.job(jobId).operationCount()) {
                return false;
            }
            if (seen[jobId][operationIndex]) {
                return false;
            }
            seen[jobId][operationIndex] = true;
        }
        return true;
    }

    public List<String> toCompactLines() {
        List<ScheduledOperation> sorted = new ArrayList<>(operations);
        sorted.sort(Comparator.comparingInt(ScheduledOperation::startTime)
                .thenComparingInt(ScheduledOperation::machineId)
                .thenComparingInt(ScheduledOperation::jobId)
                .thenComparingInt(ScheduledOperation::operationIndex));

        List<String> lines = new ArrayList<>();
        for (ScheduledOperation scheduledOperation : sorted) {
            lines.add(String.format("%-6s M%d start=%3d end=%3d duration=%2d",
                    scheduledOperation.label(),
                    scheduledOperation.machineId(),
                    scheduledOperation.startTime(),
                    scheduledOperation.endTime(),
                    scheduledOperation.operation().duration()));
        }
        return lines;
    }
}
