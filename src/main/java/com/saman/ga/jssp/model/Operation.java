package com.saman.ga.jssp.model;

/**
 * One operation of one job.
 *
 * @param jobId job index in the instance
 * @param operationIndex operation index inside the job route
 * @param machineId required machine id
 * @param duration processing time
 */
public record Operation(int jobId, int operationIndex, int machineId, int duration) {
    public Operation {
        if (jobId < 0) {
            throw new IllegalArgumentException("Job id must be non-negative");
        }
        if (operationIndex < 0) {
            throw new IllegalArgumentException("Operation index must be non-negative");
        }
        if (machineId < 0) {
            throw new IllegalArgumentException("Machine id must be non-negative");
        }
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
    }

    public String label() {
        return "J" + jobId + "-O" + operationIndex;
    }
}
