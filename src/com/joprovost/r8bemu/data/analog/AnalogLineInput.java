package com.joprovost.r8bemu.data.analog;

public interface AnalogLineInput extends AnalogInput, AnalogOutputHandler {
    default void handle(AnalogOutput state) {
        value(state.value());
    }
}
