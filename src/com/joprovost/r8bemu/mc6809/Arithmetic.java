package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.DataOutput;

import static com.joprovost.r8bemu.data.DataOutputSubset.bit;
import static com.joprovost.r8bemu.mc6809.Register.B;
import static com.joprovost.r8bemu.mc6809.Register.X;

public class Arithmetic {
    public static void sex() {
        Register.D.value(Register.B.signed());
        Register.N.set(DataOutput.negative(Register.D.value(), Register.D.mask()));
        Register.Z.set(Register.D.isClear());
    }

    public static void mul() {
        Register.D.value(Register.A.value() * Register.B.value());
        Register.Z.set(Register.D.isClear());
        Register.C.set(bit(Register.D, 7).isSet());
    }

    public static void abx() {
        X.value(X.value() + B.value());
    }

    public static void neg(DataAccess variable) {
        variable.value(~variable.value() + 1);
        Register.N.set(DataOutput.negative(variable.value(), variable.mask()));
        Register.Z.set(variable.isClear());
        Register.V.set(variable.value() == DataOutput.highestBit(variable.mask()));
        Register.C.set(variable.isClear());
    }

    public static void adc(Register register, DataAccess argument) {
        var a = register.value();
        var b = argument.value();
        var c = Register.C.value();
        var mask = register.mask();
        var result = a + b + c;
        register.value(result);

        if (mask == 0xff) Register.H.set(halfCarry(a, b, result));
        Register.C.set(carry(result, mask));
        Register.V.set(overflow(a, b, result, mask));
        Register.Z.set((result & mask) == 0);
        Register.N.set(DataOutput.negative(result, mask));
    }

    public static void add(Register register, DataAccess argument) {
        var a = register.value();
        var b = argument.value();
        var mask = register.mask();
        var result = a + b;
        register.value(result);

        if (mask == 0xff) Register.H.set(halfCarry(a, b, result));
        Register.C.set(carry(result, mask));
        Register.V.set(overflow(a, b, result, mask));
        Register.Z.set((result & mask) == 0);
        Register.N.set(DataOutput.negative(result, mask));
    }

    public static void sbc(Register register, DataAccess argument) {
        var a = register.value();
        var b = argument.value();
        var c = Register.C.value();
        var mask = register.mask();
        var result = a - b - c;
        register.value(result);

        Register.C.set(carry(result, mask));
        Register.V.set(overflow(a, b, result, mask));
        Register.Z.set((result & mask) == 0);
        Register.N.set(DataOutput.negative(result, mask));
    }

    public static void sub(Register register, DataAccess argument) {
        var a = register.value();
        var b = argument.value();
        var mask = register.mask();
        var result = a - b;
        register.value(result);

        Register.C.set(carry(result, mask));
        Register.V.set(overflow(a, b, result, mask));
        Register.Z.set((result & mask) == 0);
        Register.N.set(DataOutput.negative(result, mask));
    }

    public static void increment(DataAccess argument) {
        var a = argument.value();
        var mask = argument.mask();
        var result = a + 1;
        argument.value(result);

        Register.V.set(result == DataOutput.highestBit(mask));
        Register.N.set(DataOutput.negative(result, mask));
        Register.Z.set(argument.isClear());
    }

    public static void decrement(DataAccess argument) {
        var mask = argument.mask();
        var a = argument.value();
        var result = a - 1;
        argument.value(result);

        Register.V.set(result == DataOutput.highestBit(mask) - 1);
        Register.N.set(DataOutput.negative(result, mask));
        Register.Z.set(argument.isClear());
    }

    public static void compare(Register register, DataAccess argument) {
        var a = register.value();
        var b = argument.value();
        var mask = register.mask();
        var result = a - b;

        Register.C.set(carry(result, mask));
        Register.V.set(overflow(a, b, result, mask));
        Register.Z.set((result & mask) == 0);
        Register.N.set(DataOutput.negative(result, mask));
    }

    public static void test(DataAccess argument) {
        int result = argument.value();
        var mask = argument.mask();
        Register.V.clear();
        Register.N.set(DataOutput.negative(result, mask));
        Register.Z.set((result & mask) == 0);
    }

    private static boolean overflow(int a, int b, int result, int mask) {
        return ((a ^ b ^ result ^ (result >> 1)) & DataOutput.highestBit(mask)) != 0;
    }

    private static boolean carry(int result, int mask) {
        return (result & (mask + 1)) != 0;
    }

    private static boolean halfCarry(int a, int b, int result) {
        return ((a ^ b ^ result) & 0x10) == 0x10;
    }
}
