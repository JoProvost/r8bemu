package com.joprovost.r8bemu.data;

public interface DataInput extends LogicInput {
    default void value(DataOutput value) {
        value(value.value());
    }

    void value(int value);
}
