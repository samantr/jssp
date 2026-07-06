package com.saman.ga.jssp.dynamic;

import com.saman.ga.jssp.TestInstances;
import com.saman.ga.jssp.constraints.ConstraintEvaluation;
import com.saman.ga.jssp.constraints.ConstraintFunction;
import com.saman.ga.jssp.constraints.ConstraintLevel;
import com.saman.ga.jssp.ga.OperationBasedChromosome;
import com.saman.ga.jssp.ga.ScheduleDecoder;
import com.saman.ga.jssp.model.JsspInstance;
import com.saman.ga.jssp.model.Schedule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DynamicConstraintInterpreterTest {

    @Test
    void parsesAndEvaluatesMachineTimeLimitConstraint() {
        String json = """
                {
                  "name": "Machine 2 should not work after time 40",
                  "type": "MACHINE_TIME_LIMIT",
                  "level": "SOFT",
                  "weight": 10,
                  "parameters": {
                    "machineId": 2,
                    "latestEndTime": 40
                  }
                }
                """;

        JsspInstance instance = TestInstances.ft06();

        Schedule schedule = new ScheduleDecoder().decode(
                OperationBasedChromosome.canonical(instance),
                instance
        );

        DynamicConstraint dynamicConstraint = new DynamicConstraintParser().parse(json);

        ConstraintFunction constraintFunction = new DynamicConstraintInterpreter()
                .interpret(dynamicConstraint, instance);

        ConstraintEvaluation evaluation = constraintFunction.evaluate(schedule, instance);

        assertEquals("Machine 2 should not work after time 40", evaluation.constraintName());
        assertEquals(ConstraintLevel.SOFT, evaluation.level());

        /*
         * In the canonical ft06 decoded schedule:
         * J5-O5 runs on machine 2 and ends at time 48.
         * latestEndTime = 40
         * excess = 8
         * weight = 10
         * penalty = 80
         */
        assertEquals(80, evaluation.penalty());
        assertEquals(1, evaluation.violations().size());

        assertTrue(evaluation.hasViolation());
        assertTrue(evaluation.explanation().contains("Constraint violated"));
        assertTrue(evaluation.explanation().contains("J5-O5"));
        assertTrue(evaluation.explanation().contains("Penalty added: 80"));
        assertTrue(evaluation.explanation().contains("Total penalty added: 80"));
    }
}