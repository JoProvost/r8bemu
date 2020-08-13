package com.joprovost.r8bemu.data.link;

import com.joprovost.r8bemu.data.DataOutput;
import com.joprovost.r8bemu.data.transform.DataOutputSubset;

public interface ParallelOutput {
    void to(ParallelOutputHandler handler);

    DataOutput output();

    default DataOutput output(int mask) {
        return DataOutputSubset.of(output(), mask);
    }
}
