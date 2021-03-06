package com.joprovost.r8bemu.data.binary;

import com.joprovost.r8bemu.data.Described;
import com.joprovost.r8bemu.data.discrete.DiscreteOutput;
import com.joprovost.r8bemu.data.transform.Addition;
import com.joprovost.r8bemu.data.transform.Subtraction;

public interface BinaryOutput extends DiscreteOutput, Described {
    BinaryOutput NONE = BinaryValue.of(0, 0, "");
    BinaryOutput ONE = BinaryValue.of(1, 0b1);
    BinaryOutput TWO = BinaryValue.of(2, 0b11);

    static int signed(int value, int mask) {
        int uvalue = value & mask;
        return (negative(uvalue, mask)) ? ~mask | uvalue : uvalue;
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

    static int subset(int value, int mask) {
        return (value & mask) >> Integer.numberOfTrailingZeros(mask);
    }

    static boolean bit(int value, int pos) {
        return (value & 1 << pos) != 0;
    }

    default String hex() {
        return hex(value(), mask());
    }

    default int signed() {
        return signed(value(), mask());
    }

    default boolean isSet() {
        return value() != 0;
    }

    default int subset(int mask) {
        return subset(value(), mask);
    }

    default String description() {
        return toString();
    }

    default BinaryOutput plus(BinaryOutput offset) {
        return Addition.of(this, offset);
    }

    default BinaryOutput minus(BinaryOutput offset) {
        return Subtraction.of(this, offset);
    }

    int value();

    int mask();
}
