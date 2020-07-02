package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.DataOutput;

public class Check {
    public static void compare(Register register, DataAccess argument) {
        var a = register.value();
        var b = argument.value();
        var mask = register.mask();
        var result = a - b;

        Register.C.set(carry(result, mask));
        Register.V.set(overflow(a, b, result, mask));
        Register.Z.set(zero(result, mask));
        Register.N.set(DataOutput.negative(result, mask));
    }

    public static void test(DataAccess argument) {
        int result = argument.value();
        var mask = argument.mask();
        Register.V.clear();
        Register.N.set(DataOutput.negative(result, mask));
        Register.Z.set(zero(result, mask));
    }

    public static boolean overflow(int result, int mask) {
        return ((result ^ (result >> 1)) & DataOutput.highestBit(mask)) != 0;
    }

    public static boolean overflow(int before, int result, int mask) {
        return ((before ^ result ^ (result >> 1)) & DataOutput.highestBit(mask)) != 0;
    }

    public static boolean overflow(int a, int b, int result, int mask) {
        return ((a ^ b ^ result ^ (result >> 1)) & DataOutput.highestBit(mask)) != 0;
    }

    public static boolean carry(int result, int mask) {
        return (result & (mask + 1)) != 0;
    }

    public static boolean zero(int result, int mask) {
        return (result & mask) == 0;
    }
}
