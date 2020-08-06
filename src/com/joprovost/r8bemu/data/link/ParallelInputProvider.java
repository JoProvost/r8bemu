package com.joprovost.r8bemu.data.link;

import com.joprovost.r8bemu.data.DataAccess;

public interface ParallelInputProvider {
    void provide(DataAccess input);
}
