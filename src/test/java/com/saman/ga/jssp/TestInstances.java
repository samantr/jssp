package com.saman.ga.jssp;

import com.saman.ga.jssp.model.JsspInstance;
import com.saman.ga.jssp.parser.JsplibParser;

import java.io.IOException;

public final class TestInstances {
    private TestInstances() {
    }

    public static JsspInstance ft06() {
        try {
            return new JsplibParser().parseResource("jsplib/ft06");
        } catch (IOException e) {
            throw new IllegalStateException("Could not load ft06 test resource", e);
        }
    }
}
