package com.saman.ga.jssp.dynamic;

import com.saman.ga.jssp.model.JsspInstance;

public interface ConstraintGenerationAgent {

    DynamicConstraint generate(String naturalLanguageRequest, JsspInstance instance);
}