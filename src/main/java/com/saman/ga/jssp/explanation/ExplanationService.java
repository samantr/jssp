package com.saman.ga.jssp.explanation;

import com.saman.ga.jssp.constraints.ConstraintEvaluation;
import com.saman.ga.jssp.constraints.ConstraintFunction;
import com.saman.ga.jssp.model.JsspInstance;
import com.saman.ga.jssp.model.Schedule;

import java.util.List;
import java.util.Objects;

public final class ExplanationService {

    public ConstraintExplanationReport explain(
            Schedule schedule,
            JsspInstance instance,
            List<ConstraintFunction> constraints
    ) {
        Objects.requireNonNull(schedule, "schedule");
        Objects.requireNonNull(instance, "instance");
        Objects.requireNonNull(constraints, "constraints");

        List<ConstraintEvaluation> evaluations = constraints.stream()
                .map(constraint -> constraint.evaluate(schedule, instance))
                .toList();

        int totalPenalty = evaluations.stream()
                .mapToInt(ConstraintEvaluation::penalty)
                .sum();

        int violatedConstraintCount = (int) evaluations.stream()
                .filter(ConstraintEvaluation::hasViolation)
                .count();

        int totalViolationCount = evaluations.stream()
                .mapToInt(evaluation -> evaluation.violations().size())
                .sum();

        return new ConstraintExplanationReport(
                schedule.makespan(),
                totalPenalty,
                violatedConstraintCount,
                totalViolationCount,
                evaluations
        );
    }
}