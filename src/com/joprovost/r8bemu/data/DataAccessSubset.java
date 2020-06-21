package com.joprovost.r8bemu.data;

import java.util.Optional;

public class DataAccessSubset implements DataAccess {
    private final DataAccess origin;
    private final int mask;
    private final String description;

    private DataAccessSubset(DataAccess origin, int mask, String description) {
        this.origin = origin;
        this.mask = mask;
        this.description = description;
    }

    public static DataAccessSubset of(DataAccess origin, int mask) {
        return new DataAccessSubset(origin, mask, null);
    }

    public static DataAccessSubset lsb(DataAccess origin) {
        return DataAccessSubset.of(origin, 0x00ff);
    }

    public static DataAccessSubset msb(DataAccess origin) {
        return DataAccessSubset.of(origin, 0xff00);
    }

    public static DataAccessSubset bit(DataAccess origin, int bit) {
        return DataAccessSubset.of(origin, 1 << bit);
    }

    public DataAccessSubset describedAs(String description) {
        return new DataAccessSubset(origin, mask, description);
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
