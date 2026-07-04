package com.saman.ga.jssp.parser;

import com.saman.ga.jssp.model.Job;
import com.saman.ga.jssp.model.JsspInstance;
import com.saman.ga.jssp.model.Operation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Parser for the common JSPLIB job-shop format:
 *
 * <pre>
 * number_of_jobs number_of_machines
 * machine duration machine duration ...
 * ... one row per job
 * </pre>
 *
 * Lines starting with {@code #} and blank lines are ignored.
 * Machine ids are kept as-is, so ft06's zero-based machine ids remain zero-based.
 */
public final class JsplibParser {

    public JsspInstance parse(Path path) throws IOException {
        Objects.requireNonNull(path, "path");
        try (InputStream inputStream = Files.newInputStream(path)) {
            String fileName = path.getFileName() == null ? "instance" : path.getFileName().toString();
            return parse(inputStream, fileName);
        }
    }

    public JsspInstance parseResource(String resourcePath) throws IOException {
        Objects.requireNonNull(resourcePath, "resourcePath");
        InputStream inputStream = JsplibParser.class.getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IOException("Resource not found: " + resourcePath);
        }
        try (inputStream) {
            String name = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
            return parse(inputStream, name);
        }
    }

    public JsspInstance parse(InputStream inputStream, String instanceName) throws IOException {
        Objects.requireNonNull(inputStream, "inputStream");
        Objects.requireNonNull(instanceName, "instanceName");

        List<String> logicalLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = stripInlineComment(line).trim();
                if (!trimmed.isBlank()) {
                    logicalLines.add(trimmed);
                }
            }
        }

        if (logicalLines.isEmpty()) {
            throw new IllegalArgumentException("Empty JSPLIB instance: " + instanceName);
        }

        int[] header = parseIntegerLine(logicalLines.get(0));
        if (header.length != 2) {
            throw new IllegalArgumentException("Header must contain exactly two integers: jobs machines");
        }

        int numberOfJobs = header[0];
        int numberOfMachines = header[1];
        if (numberOfJobs <= 0 || numberOfMachines <= 0) {
            throw new IllegalArgumentException("Header values must be positive");
        }

        if (logicalLines.size() - 1 < numberOfJobs) {
            throw new IllegalArgumentException("Expected " + numberOfJobs + " job rows but found " + (logicalLines.size() - 1));
        }

        List<Job> jobs = new ArrayList<>();
        for (int jobId = 0; jobId < numberOfJobs; jobId++) {
            int[] row = parseIntegerLine(logicalLines.get(jobId + 1));
            int expectedValues = numberOfMachines * 2;
            if (row.length != expectedValues) {
                throw new IllegalArgumentException("Job row " + jobId + " must contain " + expectedValues + " integers");
            }

            List<Operation> operations = new ArrayList<>();
            for (int operationIndex = 0; operationIndex < numberOfMachines; operationIndex++) {
                int machineId = row[operationIndex * 2];
                int duration = row[operationIndex * 2 + 1];
                operations.add(new Operation(jobId, operationIndex, machineId, duration));
            }
            jobs.add(new Job(jobId, operations));
        }

        return new JsspInstance(instanceName, numberOfJobs, numberOfMachines, jobs);
    }

    private static String stripInlineComment(String line) {
        int commentIndex = line.indexOf('#');
        if (commentIndex >= 0) {
            return line.substring(0, commentIndex);
        }
        return line;
    }

    private static int[] parseIntegerLine(String line) {
        String[] tokens = line.trim().split("\\s+");
        int[] values = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            try {
                values[i] = Integer.parseInt(tokens[i]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid integer token '" + tokens[i] + "' in line: " + line, e);
            }
        }
        return values;
    }
}
