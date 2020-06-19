package com.joprovost.r8bemu.data;

public interface DataInput extends LogicInput {
    default void set(DataOutput value) {
        set(value.unsigned());
    }

    void set(int value);
}
