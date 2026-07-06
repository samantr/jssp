package com.saman.ga.jssp.explanation;

import com.saman.ga.jssp.TestInstances;
import com.saman.ga.jssp.dynamic.DynamicConstraint;
import com.saman.ga.jssp.dynamic.DynamicConstraintDecisionExplainer;
import com.saman.ga.jssp.dynamic.DynamicConstraintParser;
import com.saman.ga.jssp.model.JsspInstance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DynamicConstraintDecisionExplainerTest {

    @Test
    void explainsAcceptedDynamicConstraint() {
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
        DynamicConstraint constraint = new DynamicConstraintParser().parse(json);

        String explanation = new DynamicConstraintDecisionExplainer()
                .explainAccepted(constraint, instance);

        assertTrue(explanation.contains("Dynamic constraint accepted"));
        assertTrue(explanation.contains("Machine 2 should not work after time 40"));
        assertTrue(explanation.contains("MACHINE_TIME_LIMIT"));
        assertTrue(explanation.contains("SOFT"));
        assertTrue(explanation.contains("Weight: 10"));
        assertTrue(explanation.contains("machineId = 2"));
        assertTrue(explanation.contains("latestEndTime = 40"));
    }

    @Test
    void explainsRejectedDynamicConstraint() {
        RuntimeException exception = new IllegalArgumentException(
                "Invalid machineId 999 for constraint 'Invalid machine test'. Valid range is 0 to 5"
        );

        String explanation = new DynamicConstraintDecisionExplainer()
                .explainRejected("invalid_machine_time_limit.json", exception);

        assertTrue(explanation.contains("Dynamic constraint rejected"));
        assertTrue(explanation.contains("invalid_machine_time_limit.json"));
        assertTrue(explanation.contains("Invalid machineId 999"));
        assertTrue(explanation.contains("not added to the GA fitness calculation"));
    }
}