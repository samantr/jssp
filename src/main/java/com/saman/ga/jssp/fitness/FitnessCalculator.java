package com.saman.ga.jssp.fitness;

import com.saman.ga.jssp.constraints.ConstraintEvaluation;
import com.saman.ga.jssp.constraints.ConstraintFunction;
import com.saman.ga.jssp.model.JsspInstance;
import com.saman.ga.jssp.model.Schedule;

import java.util.List;

public class FitnessCalculator {

    private final List<ConstraintFunction> constraints;

    public FitnessCalculator(List<ConstraintFunction> constraints) {
        this.constraints = constraints;
    }

    public int calculate(Schedule schedule, JsspInstance instance) {
        return constraints.stream()
                .mapToInt(c -> c.evaluate(schedule, instance).penalty())
                .sum();
    }

    public List<ConstraintEvaluation> evaluateAll(Schedule schedule, JsspInstance instance) {
        return constraints.stream()
                .map(c -> c.evaluate(schedule, instance))
                .toList();
    }
}