package com.joprovost.r8bemu.data.binary;

import com.joprovost.r8bemu.data.transform.BinaryOutputSubset;

public interface BinaryLineOutput {
    void to(BinaryOutputHandler handler);

    BinaryOutput output();

    default BinaryOutput output(int mask) {
        return BinaryOutputSubset.of(output(), mask);
    }
}
