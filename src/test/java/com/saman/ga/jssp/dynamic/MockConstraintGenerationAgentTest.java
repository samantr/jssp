package com.saman.ga.jssp.dynamic;

import com.saman.ga.jssp.TestInstances;
import com.saman.ga.jssp.constraints.ConstraintLevel;
import com.saman.ga.jssp.model.JsspInstance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MockConstraintGenerationAgentTest {

    @Test
    void generatesMachineTimeLimitConstraintFromNaturalLanguage() {
        JsspInstance instance = TestInstances.ft06();

        DynamicConstraint constraint = new MockConstraintGenerationAgent()
                .generate("Machine 2 should not work after time 40", instance);

        assertEquals("Machine 2 should not work after time 40", constraint.name());
        assertEquals(DynamicConstraintType.MACHINE_TIME_LIMIT, constraint.type());
        assertEquals(ConstraintLevel.SOFT, constraint.level());
        assertEquals(10, constraint.weight());
        assertEquals(2, constraint.parameters().get("machineId"));
        assertEquals(40, constraint.parameters().get("latestEndTime"));
    }

    @Test
    void rejectsUnsupportedNaturalLanguageRequest() {
        JsspInstance instance = TestInstances.ft06();

        assertThrows(
                IllegalArgumentException.class,
                () -> new MockConstraintGenerationAgent()
                        .generate("Please make the schedule beautiful", instance)
        );
    }

    @Test
    void rejectsInvalidMachineIdFromNaturalLanguage() {
        JsspInstance instance = TestInstances.ft06();

        assertThrows(
                IllegalArgumentException.class,
                () -> new MockConstraintGenerationAgent()
                        .generate("Machine 999 should not work after time 40", instance)
        );
    }

    @Test
    void rejectsMissingTimeFromNaturalLanguage() {
        JsspInstance instance = TestInstances.ft06();

        assertThrows(
                IllegalArgumentException.class,
                () -> new MockConstraintGenerationAgent()
                        .generate("Machine 2 should not work late", instance)
        );
    }
}