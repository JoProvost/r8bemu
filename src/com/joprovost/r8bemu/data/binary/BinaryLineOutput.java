package com.joprovost.r8bemu.data.binary;

public interface BinaryLineOutput extends BinaryInputProvider {
    void to(BinaryOutputHandler handler);

    BinaryOutput output();

    default void provide(BinaryAccess input) {
        input.value(output());
    }
}
