package com.saman.ga.jssp.dynamic;

import com.saman.ga.jssp.constraints.ConstraintFunction;
import com.saman.ga.jssp.model.JsspInstance;

import java.util.List;
import java.util.Objects;

public final class DynamicConstraintInterpreter {

    public ConstraintFunction interpret(DynamicConstraint constraint, JsspInstance instance) {
        Objects.requireNonNull(constraint, "constraint");
        Objects.requireNonNull(instance, "instance");

        return switch (constraint.type()) {
            case MACHINE_TIME_LIMIT -> interpretMachineTimeLimit(constraint, instance);
            case JOB_FINISH_DEADLINE -> interpretJobFinishDeadline(constraint, instance);
        };
    }

    public List<ConstraintFunction> interpretAll(List<DynamicConstraint> constraints, JsspInstance instance) {
        Objects.requireNonNull(constraints, "constraints");
        Objects.requireNonNull(instance, "instance");

        return constraints.stream()
                .map(constraint -> interpret(constraint, instance))
                .toList();
    }

    private ConstraintFunction interpretMachineTimeLimit(DynamicConstraint constraint, JsspInstance instance) {
        int machineId = constraint.requiredIntParameter("machineId");
        int latestEndTime = constraint.requiredIntParameter("latestEndTime");

        if (machineId < 0 || machineId >= instance.numberOfMachines()) {
            throw new IllegalArgumentException(
                    "Invalid machineId " + machineId
                            + " for constraint '" + constraint.name() + "'. Valid range is 0 to "
                            + (instance.numberOfMachines() - 1)
            );
        }

        if (latestEndTime < 0) {
            throw new IllegalArgumentException(
                    "latestEndTime must be non-negative for constraint '" + constraint.name() + "'"
            );
        }

        return new MachineTimeLimitConstraintFunction(
                constraint.name(),
                constraint.level(),
                constraint.weight(),
                machineId,
                latestEndTime
        );
    }

    private ConstraintFunction interpretJobFinishDeadline(DynamicConstraint constraint, JsspInstance instance) {
        int jobId = constraint.requiredIntParameter("jobId");
        int deadline = constraint.requiredIntParameter("deadline");

        if (jobId < 0 || jobId >= instance.numberOfJobs()) {
            throw new IllegalArgumentException(
                    "Invalid jobId " + jobId
                            + " for constraint '" + constraint.name() + "'. Valid range is 0 to "
                            + (instance.numberOfJobs() - 1)
            );
        }

        if (deadline < 0) {
            throw new IllegalArgumentException(
                    "deadline must be non-negative for constraint '" + constraint.name() + "'"
            );
        }

        return new JobFinishDeadlineConstraintFunction(
                constraint.name(),
                constraint.level(),
                constraint.weight(),
                jobId,
                deadline
        );
    }
}