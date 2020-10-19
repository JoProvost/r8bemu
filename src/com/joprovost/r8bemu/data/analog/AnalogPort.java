package com.joprovost.r8bemu.data.analog;

import com.joprovost.r8bemu.data.discrete.DiscreteAccess;

public interface AnalogPort extends DiscreteAccess, AnalogLineInput, AnalogLineOutput, AnalogOutputHandler {
    default void handle(AnalogOutput state) {
        value(state.value());
    }
}
