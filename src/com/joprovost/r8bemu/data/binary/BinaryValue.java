package com.joprovost.r8bemu.data.binary;

import java.util.Optional;

public class BinaryValue implements BinaryOutput {

    private final int value;
    private final int mask;
    private final String description;

    private BinaryValue(int value, int mask, String description) {
        this.value = value;
        this.mask = mask;
        this.description = description;
    }

    public static BinaryValue of(int value, int mask, String description) {
        return new BinaryValue(value & mask, mask, description);
    }

    public static BinaryValue of(int value, int mask) {
        return BinaryValue.of(value, mask, null);
    }

    public static BinaryValue asByte(int value) {
        return BinaryValue.of(value, 0xff);
    }

    public static BinaryValue of(BinaryOutput address, String description) {
        return BinaryValue.of(address.value(), address.mask(), description);
    }

    public static BinaryValue value(BinaryAccess data) {
        return BinaryValue.of(data.value(), data.mask(), "$" + data.hex());
    }

    public static BinaryValue of(BinaryOutput dataAccess) {
        return BinaryValue.of(dataAccess.value(), dataAccess.mask(), dataAccess.description());
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
