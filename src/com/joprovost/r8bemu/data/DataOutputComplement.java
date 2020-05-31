package com.joprovost.r8bemu.data;

public class DataOutputComplement implements DataOutput {
    private final DataOutput origin;

    private DataOutputComplement(DataOutput origin) {
        this.origin = origin;
    }

    public static DataOutputComplement of(DataOutput origin) {
        return new DataOutputComplement(origin);
    }

    @Override
    public int unsigned() {
        return (~origin.unsigned()) & mask();
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
