package com.joprovost.r8bemu.data.discrete;

public interface DiscretePort extends DiscreteAccess, DiscreteLineInput, DiscreteLineOutput, DiscteteOutputHandler {
    default void handle(DiscreteOutput state) {
        set(state.isSet());
    }
}
