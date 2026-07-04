package com.saman.ga.jssp.constraints;

import com.saman.ga.jssp.TestInstances;
import com.saman.ga.jssp.ga.OperationBasedChromosome;
import com.saman.ga.jssp.ga.ScheduleDecoder;
import com.saman.ga.jssp.model.JsspInstance;
import com.saman.ga.jssp.model.Schedule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MakespanObjectiveTest {

    @Test
    void returnsScheduleMakespanAsObjectivePenalty() {
        JsspInstance instance = TestInstances.ft06();

        Schedule schedule = new ScheduleDecoder().decode(
                OperationBasedChromosome.canonical(instance),
                instance
        );

        ConstraintEvaluation evaluation = new MakespanObjective()
                .evaluate(schedule, instance);

        assertEquals(60, schedule.makespan());
        assertEquals(schedule.makespan(), evaluation.penalty());
        assertEquals(ConstraintLevel.OBJECTIVE, evaluation.level());
        assertEquals("Minimize makespan", evaluation.constraintName());
    }
}