package com.saman.ga.jssp.dynamic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public final class DynamicConstraintParser {

    private final ObjectMapper objectMapper;

    public DynamicConstraintParser() {
        this.objectMapper = JsonMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .build();
    }

    public DynamicConstraint parse(String json) {
        try {
            return objectMapper.readValue(json, DynamicConstraint.class);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException(
                    "Invalid dynamic constraint JSON: " + ex.getOriginalMessage(),
                    ex
            );
        }
    }

    public DynamicConstraint parse(Path path) {
        try {
            return objectMapper.readValue(path.toFile(), DynamicConstraint.class);
        } catch (IOException ex) {
            throw new IllegalArgumentException(
                    "Could not read dynamic constraint JSON from path: " + path,
                    ex
            );
        }
    }

    public List<DynamicConstraint> parseList(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException(
                    "Invalid dynamic constraint JSON list: " + ex.getOriginalMessage(),
                    ex
            );
        }
    }

    public List<DynamicConstraint> parseList(Path path) {
        try {
            return objectMapper.readValue(path.toFile(), new TypeReference<>() {
            });
        } catch (IOException ex) {
            throw new IllegalArgumentException(
                    "Could not read dynamic constraint JSON list from path: " + path,
                    ex
            );
        }
    }
}