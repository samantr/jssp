package com.saman.ga.jssp.dynamic;

import com.saman.ga.jssp.constraints.ConstraintLevel;
import com.saman.ga.jssp.model.JsspInstance;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * First mock implementation of the natural-language constraint generator.
 *
 * This class does not call any external AI API.
 * It maps simple English requests into DynamicConstraint objects.
 */
public final class MockConstraintGenerationAgent implements ConstraintGenerationAgent {

    private static final Pattern MACHINE_PATTERN = Pattern.compile(
            "\\bmachine\\s+(\\d+)\\b",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern AFTER_TIME_PATTERN = Pattern.compile(
            "\\bafter\\s+(?:time\\s+)?(\\d+)\\b",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern LATEST_END_TIME_PATTERN = Pattern.compile(
            "\\b(?:latest\\s+end\\s+time|end\\s+time|time)\\s+(?:is\\s+)?(\\d+)\\b",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public DynamicConstraint generate(String naturalLanguageRequest, JsspInstance instance) {
        Objects.requireNonNull(instance, "instance");

        if (naturalLanguageRequest == null || naturalLanguageRequest.isBlank()) {
            throw new IllegalArgumentException("Natural language request is required");
        }

        String request = naturalLanguageRequest.trim();
        String normalized = request.toLowerCase(Locale.ROOT);

        if (looksLikeMachineTimeLimit(normalized)) {
            return generateMachineTimeLimit(request, instance);
        }

        throw new IllegalArgumentException(
                "Mock agent could not map this request to a supported dynamic constraint: "
                        + naturalLanguageRequest
        );
    }

    private boolean looksLikeMachineTimeLimit(String normalizedRequest) {
        return normalizedRequest.contains("machine")
                && (
                normalizedRequest.contains("after")
                        || normalizedRequest.contains("not work")
                        || normalizedRequest.contains("should not work")
                        || normalizedRequest.contains("latest end")
        );
    }

    private DynamicConstraint generateMachineTimeLimit(String request, JsspInstance instance) {
        int machineId = extractRequiredInt(
                MACHINE_PATTERN,
                request,
                "machine id"
        );

        int latestEndTime = extractLatestEndTime(request);

        if (machineId < 0 || machineId >= instance.numberOfMachines()) {
            throw new IllegalArgumentException(
                    "Invalid machine id " + machineId
                            + ". Instance '" + instance.name()
                            + "' has machines 0 to " + (instance.numberOfMachines() - 1)
            );
        }

        if (latestEndTime < 0) {
            throw new IllegalArgumentException("Latest end time must be non-negative");
        }

        return new DynamicConstraint(
                request,
                DynamicConstraintType.MACHINE_TIME_LIMIT,
                ConstraintLevel.SOFT,
                10,
                Map.of(
                        "machineId", machineId,
                        "latestEndTime", latestEndTime
                )
        );
    }

    private int extractLatestEndTime(String request) {
        Matcher afterMatcher = AFTER_TIME_PATTERN.matcher(request);

        if (afterMatcher.find()) {
            return Integer.parseInt(afterMatcher.group(1));
        }

        Matcher latestMatcher = LATEST_END_TIME_PATTERN.matcher(request);

        if (latestMatcher.find()) {
            return Integer.parseInt(latestMatcher.group(1));
        }

        throw new IllegalArgumentException(
                "Could not extract latest end time. Example: 'Machine 2 should not work after time 40'"
        );
    }

    private int extractRequiredInt(Pattern pattern, String request, String fieldName) {
        Matcher matcher = pattern.matcher(request);

        if (!matcher.find()) {
            throw new IllegalArgumentException(
                    "Could not extract " + fieldName + " from request: " + request
            );
        }

        return Integer.parseInt(matcher.group(1));
    }
}