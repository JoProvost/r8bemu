package com.joprovost.r8bemu.data.binary;

import com.joprovost.r8bemu.data.discrete.DiscreteOutput;

public interface BinaryInputProvider {
    static BinaryInputProvider pin(int mask, DiscreteOutput out) {
        return in -> {
            if (out.isSet()) in.set(mask);
            else in.clear(mask);
        };
    }

    void provide(BinaryAccess input);
}
