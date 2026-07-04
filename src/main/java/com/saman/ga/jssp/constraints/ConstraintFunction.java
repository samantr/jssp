package com.saman.ga.jssp.constraints;

import com.saman.ga.jssp.model.JsspInstance;
import com.saman.ga.jssp.model.Schedule;

public interface ConstraintFunction {

    ConstraintEvaluation evaluate(Schedule schedule, JsspInstance instance);
}