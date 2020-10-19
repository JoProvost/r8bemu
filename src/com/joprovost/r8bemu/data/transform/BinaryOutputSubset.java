package com.joprovost.r8bemu.data.transform;

import com.joprovost.r8bemu.data.binary.BinaryOutput;

import java.util.Optional;

public class BinaryOutputSubset implements BinaryOutput {
    protected final BinaryOutput origin;
    protected final int subset;
    private final String description;

    protected BinaryOutputSubset(BinaryOutput origin, int subset, String description) {
        this.origin = origin;
        this.subset = subset;
        this.description = description;
    }

    public static BinaryOutputSubset of(BinaryOutput origin, int mask) {
        return new BinaryOutputSubset(origin, mask, null);
    }

    public static BinaryOutputSubset bit(BinaryOutput origin, int bit) {
        return BinaryOutputSubset.of(origin, 1 << bit);
    }

    public static int bit(int bit, int value) {
        return (value >> bit) & 0b1;
    }

    @Override
    public int value() {
        return origin.subset(subset);
    }

    @Override
    public int mask() {
        return subset >> Integer.numberOfTrailingZeros(subset);
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
