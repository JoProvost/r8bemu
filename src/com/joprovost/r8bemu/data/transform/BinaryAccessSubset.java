package com.joprovost.r8bemu.data.transform;

import com.joprovost.r8bemu.data.binary.BinaryAccess;
import com.joprovost.r8bemu.data.discrete.DiscreteAccess;

import java.util.Optional;

public class BinaryAccessSubset implements BinaryAccess, DiscreteAccess {
    private final BinaryAccess origin;
    private final int mask;
    private final String description;

    private BinaryAccessSubset(BinaryAccess origin, int mask, String description) {
        this.origin = origin;
        this.mask = mask;
        this.description = description;
    }

    public static BinaryAccessSubset of(BinaryAccess origin, int mask) {
        return new BinaryAccessSubset(origin, mask, null);
    }

    public static BinaryAccessSubset lsb(BinaryAccess origin) {
        return BinaryAccessSubset.of(origin, 0x00ff);
    }

    public static BinaryAccessSubset msb(BinaryAccess origin) {
        return BinaryAccessSubset.of(origin, 0xff00);
    }

    public static BinaryAccessSubset bit(BinaryAccess origin, int bit) {
        return BinaryAccessSubset.of(origin, 1 << bit);
    }

    public BinaryAccessSubset describedAs(String description) {
        return new BinaryAccessSubset(origin, mask, description);
    }

    @Override
    public void value(int value) {
        origin.value((origin.value() & ~mask) | ((value & mask()) << Integer.numberOfTrailingZeros(mask)));
    }

    @Override
    public int value() {
        return (origin.value() & mask) >> Integer.numberOfTrailingZeros(mask);
    }

    @Override
    public int mask() {
        return mask >> Integer.numberOfTrailingZeros(mask);
    }

    public boolean matches(int value) {
        return (origin.value() & mask) == (value & mask);
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
