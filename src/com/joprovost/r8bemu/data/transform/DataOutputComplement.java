package com.joprovost.r8bemu.data.transform;

import com.joprovost.r8bemu.data.DataOutput;

public class DataOutputComplement implements DataOutput {
    private final DataOutput origin;

    private DataOutputComplement(DataOutput origin) {
        this.origin = origin;
    }

    public static DataOutputComplement of(DataOutput origin) {
        return new DataOutputComplement(origin);
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
