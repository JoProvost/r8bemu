package com.joprovost.r8bemu.data;

import java.util.Optional;

public class Value implements DataOutput {

    private final int value;
    private final int mask;
    private final String description;

    private Value(int value, int mask, String description) {
        this.value = value;
        this.mask = mask;
        this.description = description;
    }

    public static Value of(int value, int mask, String description) {
        return new Value(value & mask, mask, description);
    }

    public static Value of(int value, int mask) {
        return Value.of(value, mask, null);
    }

    public static Value asByte(int value) {
        return Value.of(value, 0xff);
    }

    public static Value of(DataOutput address, String description) {
        return Value.of(address.value(), address.mask(), description);
    }

    public static Value value(DataAccess data) {
        return Value.of(data.value(), data.mask(), "$" + data.hex());
    }

    public static Value of(DataOutput dataAccess) {
        return Value.of(dataAccess.value(), dataAccess.mask(), dataAccess.description());
    }

    @Override
    public int value() { return value & mask(); }

    @Override
    public int mask() { return mask; }

    @Override
    public String description() {
        return Optional.ofNullable(description).orElse(toString());
    }

    @Override
    public String toString() {
        return "#$" + hex();
    }
}
