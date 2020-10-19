package com.joprovost.r8bemu.data.discrete;

public interface DiscreteInput {
    void set(boolean value);

    default void set() {
        set(true);
    }

    default void clear() {
        set(false);
    }
}
