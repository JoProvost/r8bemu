package com.joprovost.r8bemu.data.discrete;

public interface DiscreteLineInput extends DiscreteInput, DiscteteOutputHandler {
    default void pulse() {
        set();
        clear();
    }

    default void handle(DiscreteOutput state) {
        set(state.isSet());
    }
}
