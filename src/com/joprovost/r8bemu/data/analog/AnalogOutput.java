package com.joprovost.r8bemu.data.analog;

import com.joprovost.r8bemu.data.discrete.DiscreteOutput;

public interface AnalogOutput extends DiscreteOutput {
    default boolean isSet() {
        return value() >= 0.0;
    }

    double value();
}
