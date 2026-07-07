# JSSP GA ft06

Minimal Java 17 Maven project for Job Shop Scheduling with a Genetic Algorithm.

## Current prototype status

This repository is a Java research prototype for:

**Dynamic Natural-Language-Generated Fitness Constraints for Genetic Algorithm-Based Job Shop Scheduling**

The current version supports:

- JSPLIB ft06 parsing
- Operation-based chromosome representation
- Feasible schedule decoding
- Genetic Algorithm optimization
- ConstraintFunction-based fitness calculation
- Runtime dynamic JSON constraints
- Dynamic constraint validation
- Violation explanation reports
- Accepted/rejected dynamic constraint explanations

## Mock natural-language constraint generation

The project now includes a first mock natural-language constraint generation agent.

This does not call any external AI API.

The current flow is:

```text
natural language request
  -> MockConstraintGenerationAgent
  -> DynamicConstraint
  -> DynamicConstraintInterpreter
  -> ConstraintFunction
  -> GA fitness penalty
  -> ExplanationService
  
Current implemented dynamic constraint type:

- `MACHINE_TIME_LIMIT`

The current fitness formula is:

```text
fitness = makespan + dynamicPenalty

Current scope:

- JSPLIB parser
- JSSP model classes
- Operation-based chromosome
- Feasible schedule decoder
- Makespan fitness
- Basic GA loop
- CLI runner
- JUnit tests for parser, decoding, precedence, machine conflicts, and makespan

Not included yet:

- Dynamic constraints
- Natural language / AI agent
- JSON DSL
- Spring Boot
- UI

## Run tests

```bash
mvn test
```

## Run the CLI

Default run uses bundled `src/main/resources/jsplib/ft06`:

```bash
mvn package
java -jar target/jssp-ga-ft06-0.1.0-SNAPSHOT.jar
```

With custom parameters:

```bash
java -jar target/jssp-ga-ft06-0.1.0-SNAPSHOT.jar \
  --instance src/main/resources/jsplib/ft06 \
  --population 100 \
  --generations 500 \
  --crossoverRate 0.9 \
  --mutationRate 0.2 \
  --seed 42
```

The optimal known makespan for `ft06` is 55. This minimal GA is stochastic, so it may or may not reach 55 in a short run.
