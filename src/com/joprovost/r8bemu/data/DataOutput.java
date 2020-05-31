package com.joprovost.r8bemu.data;

import com.joprovost.r8bemu.arithmetic.Addition;
import com.joprovost.r8bemu.arithmetic.Operation;
import com.joprovost.r8bemu.arithmetic.Subtraction;

public interface DataOutput extends Described {
    static boolean negative(int value, int mask) {
        return ((value & Integer.highestOneBit(mask)) != 0);
    }

    static int signed(int value, int mask) {
        int uvalue = value & mask;
        return (negative(uvalue, mask)) ?  ~mask | uvalue : uvalue;
    }

    static String hex(int value, int mask) {
        int size = Integer.toHexString(mask).length();
        String hex = Integer.toHexString(value);
        return "0".repeat(size - hex.length()) + hex;
    }

    default String hex() {
        return DataOutput.hex(unsigned(), mask());
    }

    default int signed() {
        return DataOutput.signed(unsigned(), mask());
    }

    default boolean isSet() {
        return unsigned() != 0;
    }

    int unsigned();

    int mask();

    default String description() {
        return toString();
    }

    default boolean isClear() {
        return unsigned() == 0;
    }

    default Operation plus(DataOutput offset) {
        return Addition.of(this, offset);
    }

    default Operation minus(DataOutput offset) {
        return Subtraction.of(this, offset);
    }
}
