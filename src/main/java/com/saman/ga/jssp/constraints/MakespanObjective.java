package com.saman.ga.jssp.constraints;

import com.saman.ga.jssp.model.JsspInstance;
import com.saman.ga.jssp.model.Schedule;

import java.util.List;

public class MakespanObjective implements ConstraintFunction {

    @Override
    public ConstraintEvaluation evaluate(Schedule schedule, JsspInstance instance) {
        int makespan = schedule.makespan();

        return new ConstraintEvaluation(
                "Minimize makespan",
                ConstraintLevel.OBJECTIVE,
                makespan,
                List.of(),
                "Makespan is " + makespan + "."
        );
    }
}