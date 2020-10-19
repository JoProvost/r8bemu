package com.joprovost.r8bemu.data.binary;

public interface BinaryLineInput {
    void from(BinaryInputProvider provider);

    BinaryAccess input();
}
