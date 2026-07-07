package com.saman.ga.jssp.dynamic;

import com.saman.ga.jssp.constraints.ConstraintEvaluation;
import com.saman.ga.jssp.constraints.ConstraintFunction;
import com.saman.ga.jssp.constraints.ConstraintLevel;
import com.saman.ga.jssp.constraints.ConstraintViolation;
import com.saman.ga.jssp.model.JsspInstance;
import com.saman.ga.jssp.model.Schedule;
import com.saman.ga.jssp.model.ScheduledOperation;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class JobFinishDeadlineConstraintFunction implements ConstraintFunction {

    private final String name;
    private final ConstraintLevel level;
    private final int weight;
    private final int jobId;
    private final int deadline;

    public JobFinishDeadlineConstraintFunction(
            String name,
            ConstraintLevel level,
            int weight,
            int jobId,
            int deadline
    ) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Constraint name is required");
        }

        this.name = name;
        this.level = Objects.requireNonNull(level, "level");

        if (weight <= 0) {
            throw new IllegalArgumentException("Constraint weight must be positive");
        }

        if (jobId < 0) {
            throw new IllegalArgumentException("jobId must be non-negative");
        }

        if (deadline < 0) {
            throw new IllegalArgumentException("deadline must be non-negative");
        }

        this.weight = weight;
        this.jobId = jobId;
        this.deadline = deadline;
    }

    @Override
    public ConstraintEvaluation evaluate(Schedule schedule, JsspInstance instance) {
        Objects.requireNonNull(schedule, "schedule");
        Objects.requireNonNull(instance, "instance");

        if (jobId >= instance.numberOfJobs()) {
            throw new IllegalArgumentException(
                    "Invalid jobId " + jobId
                            + ". Instance has jobs 0 to "
                            + (instance.numberOfJobs() - 1)
            );
        }

        List<ScheduledOperation> jobOperations = schedule.operationsForJob(jobId);

        if (jobOperations.isEmpty()) {
            throw new IllegalArgumentException("No scheduled operations found for job " + jobId);
        }

        ScheduledOperation finalOperation = jobOperations.stream()
                .max(Comparator.comparingInt(ScheduledOperation::endTime))
                .orElseThrow();

        int finishTime = finalOperation.endTime();

        if (finishTime <= deadline) {
            return ConstraintEvaluation.ok(name, level);
        }

        int lateness = finishTime - deadline;
        int penalty = lateness * weight;

        String message = "Job " + jobId
                + " finishes at time " + finishTime
                + ". Deadline: " + deadline
                + ". Lateness: " + lateness
                + ". Weight: " + weight
                + ". Penalty added: " + penalty + ".";

        ConstraintViolation violation = ConstraintViolation.forOperation(
                message,
                penalty,
                finalOperation
        );

        return new ConstraintEvaluation(
                name,
                level,
                penalty,
                List.of(violation),
                buildExplanation(violation, penalty)
        );
    }

    private String buildExplanation(ConstraintViolation violation, int penalty) {
        return "Constraint violated: " + name
                + System.lineSeparator()
                + violation.message()
                + System.lineSeparator()
                + "Total penalty added: " + penalty + ".";
    }
}