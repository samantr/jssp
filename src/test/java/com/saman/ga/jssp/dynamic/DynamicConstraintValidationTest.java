package com.saman.ga.jssp.dynamic;

import com.saman.ga.jssp.TestInstances;
import com.saman.ga.jssp.model.JsspInstance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class DynamicConstraintValidationTest {

    @Test
    void rejectsUnsupportedConstraintType() {
        String json = """
                {
                  "name": "Unsupported test constraint",
                  "type": "UNKNOWN_TYPE",
                  "level": "SOFT",
                  "weight": 10,
                  "parameters": {
                    "machineId": 2,
                    "latestEndTime": 40
                  }
                }
                """;

        DynamicConstraintParser parser = new DynamicConstraintParser();

        assertThrows(IllegalArgumentException.class, () -> parser.parse(json));
    }

    @Test
    void rejectsMissingRequiredMachineTimeLimitParameter() {
        String json = """
                {
                  "name": "Machine 2 should not work after time 40",
                  "type": "MACHINE_TIME_LIMIT",
                  "level": "SOFT",
                  "weight": 10,
                  "parameters": {
                    "machineId": 2
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
    void rejectsInvalidMachineId() {
        String json = """
                {
                  "name": "Invalid machine test",
                  "type": "MACHINE_TIME_LIMIT",
                  "level": "SOFT",
                  "weight": 10,
                  "parameters": {
                    "machineId": 999,
                    "latestEndTime": 40
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
    void rejectsNonPositiveWeight() {
        String json = """
                {
                  "name": "Invalid weight test",
                  "type": "MACHINE_TIME_LIMIT",
                  "level": "SOFT",
                  "weight": 0,
                  "parameters": {
                    "machineId": 2,
                    "latestEndTime": 40
                  }
                }
                """;

        DynamicConstraintParser parser = new DynamicConstraintParser();

        assertThrows(IllegalArgumentException.class, () -> parser.parse(json));
    }

    @Test
    void rejectsUnknownJsonField() {
        String json = """
                {
                  "name": "Unknown field test",
                  "type": "MACHINE_TIME_LIMIT",
                  "level": "SOFT",
                  "weight": 10,
                  "unknownField": "should fail",
                  "parameters": {
                    "machineId": 2,
                    "latestEndTime": 40
                  }
                }
                """;

        DynamicConstraintParser parser = new DynamicConstraintParser();

        assertThrows(IllegalArgumentException.class, () -> parser.parse(json));
    }
}