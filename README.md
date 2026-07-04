# JSSP GA ft06

Minimal Java 17 Maven project for Job Shop Scheduling with a Genetic Algorithm.

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
