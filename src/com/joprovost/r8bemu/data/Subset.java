package com.joprovost.r8bemu.data;

import java.util.Optional;

public class Subset implements DataAccess {
    private final DataAccess origin;
    private final int mask;
    private final String description;

    private Subset(DataAccess origin, int mask, String description) {
        this.origin = origin;
        this.mask = mask;
        this.description = description;
    }

    public static Subset of(DataAccess origin, int mask) {
        return new Subset(origin, mask, null);
    }

    public static Subset lsb(DataAccess origin) {
        return Subset.of(origin, 0x00ff);
    }

    public static Subset msb(DataAccess origin) {
        return Subset.of(origin, 0xff00);
    }

    public static Subset bit(DataAccess origin, int bit) {
        return Subset.of(origin, 1 << bit);
    }

    public Subset describedAs(String description) {
        return new Subset(origin, mask, description);
    }

    @Override
    public void set(int value) {
        origin.set((origin.unsigned() & ~mask) | ((value & mask()) << Integer.numberOfTrailingZeros(mask)));
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
