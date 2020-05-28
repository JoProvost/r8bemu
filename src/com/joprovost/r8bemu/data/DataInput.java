package com.joprovost.r8bemu.data;

public interface DataInput {
    default void set() {
        set(true);
    }

    default void clear() {
        set(false);
    }

    default void set(DataOutput value) {
        set(value.unsigned());
    }

    void set(boolean value);

    void set(int value);
}
