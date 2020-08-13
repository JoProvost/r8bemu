package com.joprovost.r8bemu.data.link;

import com.joprovost.r8bemu.data.BitOutput;
import com.joprovost.r8bemu.data.DataAccess;

public interface ParallelInputProvider {
    static ParallelInputProvider pin(int mask, BitOutput out) {
        return in -> {
            if (out.isSet()) in.set(mask);
            else in.clear(mask);
        };
    }

    void provide(DataAccess input);
}
