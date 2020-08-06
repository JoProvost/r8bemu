package com.joprovost.r8bemu.data.transform;

import com.joprovost.r8bemu.data.DataOutput;

import java.util.Optional;

public class DataOutputSubset implements DataOutput {
    protected final DataOutput origin;
    protected final int subset;
    private final String description;

    protected DataOutputSubset(DataOutput origin, int subset, String description) {
        this.origin = origin;
        this.subset = subset;
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
        return new DataOutputSubset(origin, subset, description);
    }

    @Override
    public int value() {
        return origin.subset(subset);
    }

    @Override
    public int mask() {
        return subset >> Integer.numberOfTrailingZeros(subset);
    }

    public boolean matches(int value) {
        return (origin.value() & subset) == (value & subset);
    }

    @Override
    public String description() {
        return Optional.ofNullable(description).orElse("(a subset of {" + origin + "} with mask 0b" + Integer.toBinaryString(subset)+")");
    }

    @Override
    public String toString() {
        return description() +"' = $" + hex();
    }
}
