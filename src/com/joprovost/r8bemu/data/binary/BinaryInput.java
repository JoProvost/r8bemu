package com.joprovost.r8bemu.data.binary;

import com.joprovost.r8bemu.data.discrete.DiscreteInput;

public interface BinaryInput extends DiscreteInput {
    default void value(BinaryOutput value) {
        value(value.value());
    }

    void value(int value);
}
