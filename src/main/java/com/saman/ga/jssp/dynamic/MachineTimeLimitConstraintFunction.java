package com.saman.ga.jssp.dynamic;

import com.saman.ga.jssp.constraints.ConstraintEvaluation;
import com.saman.ga.jssp.constraints.ConstraintFunction;
import com.saman.ga.jssp.constraints.ConstraintLevel;
import com.saman.ga.jssp.constraints.ConstraintViolation;
import com.saman.ga.jssp.model.JsspInstance;
import com.saman.ga.jssp.model.Schedule;
import com.saman.ga.jssp.model.ScheduledOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class MachineTimeLimitConstraintFunction implements ConstraintFunction {

    private final String name;
    private final ConstraintLevel level;
    private final int weight;
    private final int machineId;
    private final int latestEndTime;

    public MachineTimeLimitConstraintFunction(
            String name,
            ConstraintLevel level,
            int weight,
            int machineId,
            int latestEndTime
    ) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Constraint name is required");
        }

        this.name = name;
        this.level = Objects.requireNonNull(level, "level");

        if (weight <= 0) {
            throw new IllegalArgumentException("Constraint weight must be positive");
        }

        if (machineId < 0) {
            throw new IllegalArgumentException("machineId must be non-negative");
        }

        if (latestEndTime < 0) {
            throw new IllegalArgumentException("latestEndTime must be non-negative");
        }

        this.weight = weight;
        this.machineId = machineId;
        this.latestEndTime = latestEndTime;
    }

    @Override
    public ConstraintEvaluation evaluate(Schedule schedule, JsspInstance instance) {
        Objects.requireNonNull(schedule, "schedule");
        Objects.requireNonNull(instance, "instance");

        if (machineId >= instance.numberOfMachines()) {
            throw new IllegalArgumentException(
                    "Invalid machineId " + machineId
                            + ". Instance has machines 0 to "
                            + (instance.numberOfMachines() - 1)
            );
        }

        List<ConstraintViolation> violations = new ArrayList<>();
        int totalPenalty = 0;

        for (ScheduledOperation operation : schedule.operationsForMachine(machineId)) {
            if (operation.endTime() > latestEndTime) {
                int excessTime = operation.endTime() - latestEndTime;
                int penalty = excessTime * weight;
                totalPenalty += penalty;

                violations.add(new ConstraintViolation(
                        "Operation " + operation.label()
                                + " on Machine " + machineId
                                + " ends at time " + operation.endTime()
                                + ". Allowed latest end time: " + latestEndTime
                                + ". Penalty added: " + penalty + ".",
                        penalty
                ));
            }
        }

        if (violations.isEmpty()) {
            return ConstraintEvaluation.ok(name, level);
        }

        return new ConstraintEvaluation(
                name,
                level,
                totalPenalty,
                List.copyOf(violations),
                buildExplanation(violations, totalPenalty)
        );
    }

    private String buildExplanation(List<ConstraintViolation> violations, int totalPenalty) {
        StringBuilder explanation = new StringBuilder();

        explanation.append("Constraint violated: ")
                .append(name)
                .append(System.lineSeparator());

        for (ConstraintViolation violation : violations) {
            explanation.append(violation.message())
                    .append(System.lineSeparator());
        }

        explanation.append("Total penalty added: ")
                .append(totalPenalty)
                .append(".");

        return explanation.toString();
    }
}