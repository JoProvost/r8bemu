package com.joprovost.r8bemu.data;

import java.util.Optional;

public class DataOutputSubset implements DataOutput {
    protected final DataOutput origin;
    protected final int mask;
    private final String description;

    protected DataOutputSubset(DataOutput origin, int mask, String description) {
        this.origin = origin;
        this.mask = mask;
        this.description = description;
    }

    public static DataOutputSubset of(DataOutput origin, int mask) {
        return new DataOutputSubset(origin, mask, null);
    }

    public static DataOutputSubset bit(DataOutput origin, int bit) {
        return DataOutputSubset.of(origin, 1 << bit);
    }

    public static int bit(int bit, int value) {
        return (value >> bit) & 0b1;
    }

    public DataOutputSubset describedAs(String description) {
        return new DataOutputSubset(origin, mask, description);
    }

    @Override
    public int unsigned() {
        return (origin.unsigned() & mask) >> Integer.numberOfTrailingZeros(mask);
    }

    @Override
    public int mask() {
        return mask >> Integer.numberOfTrailingZeros(mask);
    }

    public boolean matches(int value) {
        return (origin.unsigned() & mask) == (value & mask);
    }

    @Override
    public String description() {
        return Optional.ofNullable(description).orElse("(a subset of {" + origin + "} with mask 0b" + Integer.toBinaryString(mask)+")");
    }

    @Override
    public String toString() {
        return description() +"' = $" + hex();
    }
}
