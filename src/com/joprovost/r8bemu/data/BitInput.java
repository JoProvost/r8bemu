package com.joprovost.r8bemu.data;

public interface BitInput {
    void set(boolean value);

    default void set() {
        set(true);
    }

    default void clear() {
        set(false);
    }
}
