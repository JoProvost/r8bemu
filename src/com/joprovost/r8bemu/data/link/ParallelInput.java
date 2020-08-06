package com.joprovost.r8bemu.data.link;

import com.joprovost.r8bemu.data.DataAccess;

public interface ParallelInput {
    void inputFrom(ParallelInputProvider provider);

    DataAccess input();
}
