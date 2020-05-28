package com.joprovost.r8bemu.data;

import java.util.Optional;

public class Constant implements DataAccess {
    public static final Constant ZERO = of(0, 0b1);
    public static final Constant ONE = of(1, 0b1);
    public static final Constant TWO = of(2, 0b11);

    private final int value;
    private final int mask;
    private final String description;

    private Constant(int value, int mask, String description) {
        this.value = value;
        this.mask = mask;
        this.description = description;
    }

    public static Constant of(int value, int mask, String description) {
        return new Constant(value & mask, mask, description);
    }

    public static Constant of(int value, int mask) {
        return Constant.of(value, mask, null);
    }

    public static Constant asByte(int value) {
        return Constant.of(value, 0xff);
    }

    public static Constant address(int address, String description) {
        return Constant.of(address, 0xffff, description);
    }

    public static Constant address(DataOutput address, String description) {
        return Constant.of(address, description);
    }

    private static Constant of(DataOutput address, String description) {
        return Constant.of(address.unsigned(), address.mask(), description);
    }

    public static Constant relativeAddress(int offset, int pc) {
        return Constant.of(
                offset + pc,
                0xffff,
                relativeAddressDescription(offset, pc));
    }

    public static String relativeAddressDescription(int offset, int pc) {
        return ((offset >= 0)
                ? "PC+" + offset
                : "PC-" + Math.abs(offset))
                + "=$" + Integer.toHexString(offset + pc);
    }

    public static Constant directAddress(int offset, int page) {
        return Constant.of((page << 8) | (offset & 0xff), 0xffff, directAddressDescription(offset));
    }

    public static String directAddressDescription(int offset) {
        return ">$" + Integer.toHexString(offset);
    }

    public static Constant address(DataAccess next) {
        return Constant.of(next.unsigned(), next.mask(),  "$" + next.hex());
    }

    public static Constant value(DataAccess data) {
        return Constant.of(data.unsigned(), data.mask(),  "$" + data.hex());
    }

    public static Constant of(DataOutput dataAccess) {
        return Constant.of(dataAccess.unsigned(), dataAccess.mask(), dataAccess.description());
    }

    @Override
    public int unsigned() { return value & mask(); }

    @Override
    public int mask() { return mask; }

    @Override
    public String description() {
        return Optional.ofNullable(description).orElse(toString());
    }

    @Override
    public void set(int value) {
        throw new UnsupportedOperationException("Constant value");
    }

    @Override
    public String toString() {
        return "#$" + hex();
    }
}
