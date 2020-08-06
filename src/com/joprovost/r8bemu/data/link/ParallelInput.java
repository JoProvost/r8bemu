package com.joprovost.r8bemu.data.link;

import com.joprovost.r8bemu.data.DataAccess;

public interface ParallelInput {
    void from(ParallelInputProvider provider);

    DataAccess input();
}
