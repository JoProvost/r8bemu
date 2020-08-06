package com.joprovost.r8bemu.data.link;

import com.joprovost.r8bemu.data.DataOutput;

public interface ParallelOutput {
    void outputTo(ParallelOutputHandler handler);

    DataOutput output();
}
