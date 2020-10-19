package com.joprovost.r8bemu.data.transform;

import com.joprovost.r8bemu.data.binary.BinaryOutput;

public class BinaryOutputComplement implements BinaryOutput {
    private final BinaryOutput origin;

    private BinaryOutputComplement(BinaryOutput origin) {
        this.origin = origin;
    }

    public static BinaryOutputComplement of(BinaryOutput origin) {
        return new BinaryOutputComplement(origin);
    }

    @Override
    public int value() {
        return (~origin.value()) & mask();
    }

    @Override
    public int mask() {
        return origin.mask();
    }

    @Override
    public String description() {
        return "~" + origin.description();
    }
}
