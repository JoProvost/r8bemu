package com.joprovost.r8bemu.data.analog;

import com.joprovost.r8bemu.data.discrete.DiscreteInput;

public interface AnalogInput extends DiscreteInput {
    static AnalogInput broadcast(AnalogInput... sinks) {
        return (value) -> {
            for (var sink : sinks) {
                sink.value(value);
            }
        };
    }

    default void value(AnalogOutput value) {
        value(value.value());
    }

    void value(double value);

    default void set(boolean state) {
        value(state ? 1 : -1);
    }
}
