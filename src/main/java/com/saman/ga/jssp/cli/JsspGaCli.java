package com.saman.ga.jssp.cli;

import com.saman.ga.jssp.constraints.ConstraintFunction;
import com.saman.ga.jssp.dynamic.*;
import com.saman.ga.jssp.explanation.ConstraintExplanationReport;
import com.saman.ga.jssp.explanation.ExplanationService;
import com.saman.ga.jssp.ga.GeneticAlgorithm;
import com.saman.ga.jssp.ga.GeneticAlgorithmConfig;
import com.saman.ga.jssp.ga.GeneticAlgorithmResult;
import com.saman.ga.jssp.model.JsspInstance;
import com.saman.ga.jssp.parser.JsplibParser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple command-line entry point for the JSSP GA prototype.
 */
public final class JsspGaCli {

    private JsspGaCli() {
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> options = parseOptions(args);

        if (options.containsKey("help")) {
            printUsage();
            return;
        }

        GeneticAlgorithmConfig defaults = GeneticAlgorithmConfig.defaults();

        int population = intOption(options, "population", defaults.populationSize());
        int generations = intOption(options, "generations", defaults.generations());
        double crossoverRate = doubleOption(options, "crossoverRate", defaults.crossoverRate());
        double mutationRate = doubleOption(options, "mutationRate", defaults.mutationRate());
        int tournamentSize = intOption(options, "tournamentSize", defaults.tournamentSize());
        long seed = longOption(options, "seed", defaults.seed());
        boolean printSchedule = Boolean.parseBoolean(options.getOrDefault("printSchedule", "false"));

        JsplibParser parser = new JsplibParser();

        JsspInstance instance;
        String instanceOption = options.get("instance");

        if (instanceOption == null) {
            instance = parser.parseResource("jsplib/ft06");
        } else {
            Path instancePath = Path.of(instanceOption);

            if (!Files.exists(instancePath)) {
                throw new IllegalArgumentException("Instance file not found: " + instancePath.toAbsolutePath());
            }

            instance = parser.parse(instancePath);
        }

        List<ConstraintFunction> dynamicConstraintFunctions = loadDynamicConstraintFunctions(options, instance);

        GeneticAlgorithmConfig config = new GeneticAlgorithmConfig(
                population,
                generations,
                crossoverRate,
                mutationRate,
                tournamentSize,
                seed
        );

        System.out.println("JSSP GA - dynamic JSON prototype");
        System.out.println("Instance : " + instance.name());
        System.out.println("Jobs : " + instance.numberOfJobs());
        System.out.println("Machines : " + instance.numberOfMachines());
        System.out.println("Operations : " + instance.totalOperations());
        System.out.println("Population : " + config.populationSize());
        System.out.println("Generations : " + config.generations());
        System.out.println("Crossover rate: " + config.crossoverRate());
        System.out.println("Mutation rate : " + config.mutationRate());
        System.out.println("Seed : " + config.seed());
        System.out.println("Dynamic constraints: " + dynamicConstraintFunctions.size());

        long startedAt = System.nanoTime();

        GeneticAlgorithm ga = new GeneticAlgorithm(instance, config, dynamicConstraintFunctions);
        GeneticAlgorithmResult result = ga.run();

        long elapsedMillis = (System.nanoTime() - startedAt) / 1_000_000L;

        ConstraintExplanationReport dynamicReport = new ExplanationService()
                .explain(result.bestSchedule(), instance, dynamicConstraintFunctions);

        System.out.println();
        System.out.println("Best makespan : " + result.bestSchedule().makespan());
        System.out.println("Best fitness : " + result.bestFitness());
        System.out.println("Dynamic penalty: " + dynamicReport.totalPenalty());
        System.out.println("Generation : " + result.generationFound());
        System.out.println("Runtime ms : " + elapsedMillis);
        System.out.println("Precedence OK : " + result.bestSchedule().respectsPrecedence(instance));
        System.out.println("Machines OK : " + result.bestSchedule().hasNoMachineConflicts(instance));
        System.out.println("All ops once : " + result.bestSchedule().schedulesEveryOperationExactlyOnce(instance));
        System.out.println("Best genes : " + result.bestChromosome());

        if (dynamicReport.hasConstraints()) {
            System.out.println();
            System.out.println(dynamicReport.toText());
        }

        if (printSchedule) {
            System.out.println();
            System.out.println("Best schedule:");
            result.bestSchedule().toCompactLines().forEach(System.out::println);
        }
    }

    private static List<ConstraintFunction> loadDynamicConstraintFunctions(
            Map<String, String> options,
            JsspInstance instance
    ) {
        List<ConstraintFunction> functions = new ArrayList<>();

        String constraintOption = options.get("constraint");
        String naturalLanguageOption = options.get("nl");

        DynamicConstraintParser parser = new DynamicConstraintParser();
        DynamicConstraintInterpreter interpreter = new DynamicConstraintInterpreter();
        DynamicConstraintDecisionExplainer decisionExplainer = new DynamicConstraintDecisionExplainer();

        if (constraintOption != null && !constraintOption.isBlank()) {
            Path constraintPath = Path.of(constraintOption);

            if (!Files.exists(constraintPath)) {
                throw new IllegalArgumentException(
                        "Constraint JSON file not found: " + constraintPath.toAbsolutePath()
                );
            }

            try {
                DynamicConstraint dynamicConstraint = parser.parse(constraintPath);
                functions.add(interpreter.interpret(dynamicConstraint, instance));

                System.out.println(decisionExplainer.explainAccepted(dynamicConstraint, instance));
            } catch (RuntimeException ex) {
                String rejectionExplanation = decisionExplainer.explainRejected(
                        constraintPath.toString(),
                        ex
                );

                throw new IllegalArgumentException(rejectionExplanation, ex);
            }
        }

        if (naturalLanguageOption != null && !naturalLanguageOption.isBlank()) {
            try {
                DynamicConstraint generatedConstraint = new MockConstraintGenerationAgent()
                        .generate(naturalLanguageOption, instance);

                functions.add(interpreter.interpret(generatedConstraint, instance));

                System.out.println("Natural language request:");
                System.out.println(naturalLanguageOption);
                System.out.println();

                System.out.println("Mock agent generated dynamic constraint:");
                System.out.println("Name: " + generatedConstraint.name());
                System.out.println("Type: " + generatedConstraint.type());
                System.out.println("Level: " + generatedConstraint.level());
                System.out.println("Weight: " + generatedConstraint.weight());
                System.out.println("Parameters: " + generatedConstraint.parameters());
                System.out.println();

                System.out.println(decisionExplainer.explainAccepted(generatedConstraint, instance));
            } catch (RuntimeException ex) {
                String rejectionExplanation = decisionExplainer.explainRejected(
                        naturalLanguageOption,
                        ex
                );

                throw new IllegalArgumentException(rejectionExplanation, ex);
            }
        }

        return List.copyOf(functions);
    }
    private static Map<String, String> parseOptions(String[] args) {
        Map<String, String> options = new HashMap<>();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (!arg.startsWith("--")) {
                throw new IllegalArgumentException("Unknown argument: " + arg);
            }

            String key = arg.substring(2);

            if (key.equals("help")) {
                options.put("help", "true");
                continue;
            }

            if (i + 1 >= args.length) {
                throw new IllegalArgumentException("Missing value for option: " + arg);
            }

            options.put(key, args[++i]);
        }

        return options;
    }

    private static int intOption(Map<String, String> options, String key, int defaultValue) {
        return options.containsKey(key) ? Integer.parseInt(options.get(key)) : defaultValue;
    }

    private static long longOption(Map<String, String> options, String key, long defaultValue) {
        return options.containsKey(key) ? Long.parseLong(options.get(key)) : defaultValue;
    }

    private static double doubleOption(Map<String, String> options, String key, double defaultValue) {
        return options.containsKey(key) ? Double.parseDouble(options.get(key)) : defaultValue;
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println(" java -jar target/jssp-ga-ft06-0.1.0-SNAPSHOT.jar [options]");
        System.out.println();
        System.out.println("Options:");
        System.out.println(" --instance  JSPLIB instance path. Default: bundled jsplib/ft06");
        System.out.println(" --constraint  Dynamic constraint JSON path");
        System.out.println(" --nl  Natural language dynamic constraint request handled by mock agent");
        System.out.println(" --population  Default: 100");
        System.out.println(" --generations  Default: 500");
        System.out.println(" --crossoverRate  Default: 0.9");
        System.out.println(" --mutationRate  Default: 0.2");
        System.out.println(" --tournamentSize  Default: 3");
        System.out.println(" --seed  Default: 42");
        System.out.println(" --printSchedule  Default: false");
    }
}