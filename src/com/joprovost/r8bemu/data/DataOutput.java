package com.joprovost.r8bemu.data;

public interface DataOutput extends LogicOutput, Described {

    static int signed(int value, int mask) {
        int uvalue = value & mask;
        return (negative(uvalue, mask)) ?  ~mask | uvalue : uvalue;
    }

    static String hex(int value, int mask) {
        int size = Integer.toHexString(mask).length();
        String hex = Integer.toHexString(value);
        return "0".repeat(size - hex.length()) + hex;
    }

    static int highestBit(int mask) {
        return (mask >> 1) + 1;
    }

    static boolean negative(int result, int mask) {
        return (result & highestBit(mask)) != 0;
    }

    default String hex() {
        return hex(value(), mask());
    }

    default int signed() {
        return signed(value(), mask());
    }

    @Override
    default boolean isSet() {
        return value() != 0;
    }

    int value();

    int mask();

    default String description() {
        return toString();
    }

    @Override
    default boolean isClear() {
        return value() == 0;
    }

    default DataOutput plus(DataOutput offset) {
        return Addition.of(this, offset);
    }

    default DataOutput minus(DataOutput offset) {
        return Subtraction.of(this, offset);
    }
}
