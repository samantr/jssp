package com.saman.ga.jssp.dynamic;

import com.saman.ga.jssp.TestInstances;
import com.saman.ga.jssp.model.JsspInstance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class JobFinishDeadlineValidationTest {

    @Test
    void rejectsMissingJobId() {
        String json = """
                {
                  "name": "Job should finish before time 45",
                  "type": "JOB_FINISH_DEADLINE",
                  "level": "SOFT",
                  "weight": 10,
                  "parameters": {
                    "deadline": 45
                  }
                }
                """;

        JsspInstance instance = TestInstances.ft06();

        DynamicConstraint constraint = new DynamicConstraintParser().parse(json);
        DynamicConstraintInterpreter interpreter = new DynamicConstraintInterpreter();

        assertThrows(
                IllegalArgumentException.class,
                () -> interpreter.interpret(constraint, instance)
        );
    }

    @Test
    void rejectsMissingDeadline() {
        String json = """
                {
                  "name": "Job 3 should finish before deadline",
                  "type": "JOB_FINISH_DEADLINE",
                  "level": "SOFT",
                  "weight": 10,
                  "parameters": {
                    "jobId": 3
                  }
                }
                """;

        JsspInstance instance = TestInstances.ft06();

        DynamicConstraint constraint = new DynamicConstraintParser().parse(json);
        DynamicConstraintInterpreter interpreter = new DynamicConstraintInterpreter();

        assertThrows(
                IllegalArgumentException.class,
                () -> interpreter.interpret(constraint, instance)
        );
    }

    @Test
    void rejectsInvalidJobId() {
        String json = """
                {
                  "name": "Invalid job deadline test",
                  "type": "JOB_FINISH_DEADLINE",
                  "level": "SOFT",
                  "weight": 10,
                  "parameters": {
                    "jobId": 999,
                    "deadline": 45
                  }
                }
                """;

        JsspInstance instance = TestInstances.ft06();

        DynamicConstraint constraint = new DynamicConstraintParser().parse(json);
        DynamicConstraintInterpreter interpreter = new DynamicConstraintInterpreter();

        assertThrows(
                IllegalArgumentException.class,
                () -> interpreter.interpret(constraint, instance)
        );
    }

    @Test
    void rejectsNegativeDeadline() {
        String json = """
                {
                  "name": "Invalid deadline test",
                  "type": "JOB_FINISH_DEADLINE",
                  "level": "SOFT",
                  "weight": 10,
                  "parameters": {
                    "jobId": 3,
                    "deadline": -1
                  }
                }
                """;

        JsspInstance instance = TestInstances.ft06();

        DynamicConstraint constraint = new DynamicConstraintParser().parse(json);
        DynamicConstraintInterpreter interpreter = new DynamicConstraintInterpreter();

        assertThrows(
                IllegalArgumentException.class,
                () -> interpreter.interpret(constraint, instance)
        );
    }
}