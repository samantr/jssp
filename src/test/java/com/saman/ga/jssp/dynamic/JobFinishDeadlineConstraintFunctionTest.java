package com.saman.ga.jssp.dynamic;

import com.saman.ga.jssp.TestInstances;
import com.saman.ga.jssp.constraints.ConstraintEvaluation;
import com.saman.ga.jssp.constraints.ConstraintLevel;
import com.saman.ga.jssp.ga.OperationBasedChromosome;
import com.saman.ga.jssp.ga.ScheduleDecoder;
import com.saman.ga.jssp.model.JsspInstance;
import com.saman.ga.jssp.model.Schedule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JobFinishDeadlineConstraintFunctionTest {

    @Test
    void penalizesJobFinishingAfterDeadline() {
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

        JobFinishDeadlineConstraintFunction constraint = new JobFinishDeadlineConstraintFunction(
                "Job 3 should finish before deadline",
                ConstraintLevel.SOFT,
                10,
                jobId,
                deadline
        );

        ConstraintEvaluation evaluation = constraint.evaluate(schedule, instance);

        assertEquals("Job 3 should finish before deadline", evaluation.constraintName());
        assertEquals(ConstraintLevel.SOFT, evaluation.level());
        assertEquals(50, evaluation.penalty());
        assertEquals(1, evaluation.violations().size());

        assertTrue(evaluation.hasViolation());
        assertTrue(evaluation.explanation().contains("Constraint violated"));
        assertTrue(evaluation.explanation().contains("Job 3 finishes at time"));
        assertTrue(evaluation.explanation().contains("Penalty added: 50"));
        assertTrue(evaluation.explanation().contains("Total penalty added: 50"));
    }

    @Test
    void returnsOkWhenJobFinishesBeforeDeadline() {
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

        int deadline = jobFinishTime;

        JobFinishDeadlineConstraintFunction constraint = new JobFinishDeadlineConstraintFunction(
                "Job 3 should finish before deadline",
                ConstraintLevel.SOFT,
                10,
                jobId,
                deadline
        );

        ConstraintEvaluation evaluation = constraint.evaluate(schedule, instance);

        assertEquals(0, evaluation.penalty());
        assertEquals(0, evaluation.violations().size());
        assertTrue(!evaluation.hasViolation());
        assertEquals("No violation.", evaluation.explanation());
    }
}