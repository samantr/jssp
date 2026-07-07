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

class JobFinishDeadlineInterpreterTest {

    @Test
    void parsesInterpretsAndEvaluatesJobFinishDeadlineConstraint() {
        JsspInstance instance = TestInstances.ft06();

        Schedule schedule = new ScheduleDecoder().decode(
                OperationBasedChromosome.canonical(instance),
                instance
        );

        int jobId = 3;
        int jobFinishTime = schedule.operationsForJob(jobId).stream()
                .mapToInt(operation -> operation.endTime())
                .max()
                .orElseThrow();

        int deadline = jobFinishTime - 5;

        String json = """
                {
                  "name": "Job 3 should finish before deadline",
                  "type": "JOB_FINISH_DEADLINE",
                  "level": "SOFT",
                  "weight": 10,
                  "parameters": {
                    "jobId": 3,
                    "deadline": %d
                  }
                }
                """.formatted(deadline);

        DynamicConstraint dynamicConstraint = new DynamicConstraintParser().parse(json);

        ConstraintFunction constraintFunction = new DynamicConstraintInterpreter()
                .interpret(dynamicConstraint, instance);

        ConstraintEvaluation evaluation = constraintFunction.evaluate(schedule, instance);

        assertEquals("Job 3 should finish before deadline", evaluation.constraintName());
        assertEquals(ConstraintLevel.SOFT, evaluation.level());
        assertEquals(50, evaluation.penalty());
        assertEquals(1, evaluation.violations().size());

        assertTrue(evaluation.hasViolation());
        assertTrue(evaluation.explanation().contains("Job 3 finishes at time"));
        assertTrue(evaluation.explanation().contains("Penalty added: 50"));
    }
}