package com.joprovost.r8bemu.devices.mc6809;

import com.joprovost.r8bemu.data.binary.BinaryAccess;
import com.joprovost.r8bemu.data.binary.BinaryOutput;

import static com.joprovost.r8bemu.data.binary.BinaryOutput.negative;
import static com.joprovost.r8bemu.data.transform.BinaryOutputSubset.bit;
import static com.joprovost.r8bemu.devices.mc6809.Check.carry;
import static com.joprovost.r8bemu.devices.mc6809.Check.overflow;
import static com.joprovost.r8bemu.devices.mc6809.Check.zero;
import static com.joprovost.r8bemu.devices.mc6809.Register.B;
import static com.joprovost.r8bemu.devices.mc6809.Register.X;

public class Arithmetic {
    public static void sex() {
        Register.D.value(Register.B.signed());
        Register.N.set(negative(Register.D.value(), Register.D.mask()));
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

    public static void neg(BinaryAccess variable) {
        var before = variable.value();
        var result = ~before + 1;
        var mask = variable.mask();
        variable.value(result);

        Register.N.set(negative(result, mask));
        Register.Z.set(zero(result, mask));
        Register.V.set(overflow(before, result, mask));
        Register.C.set(carry(result, mask));
    }

    public static void adc(Register register, BinaryOutput argument) {
        var a = register.value();
        var b = argument.value();
        var c = Register.C.value();
        var mask = register.mask();
        var result = a + b + c;
        register.value(result);

        if (mask == 0xff) Register.H.set(halfCarry(a, b, result));
        Register.C.set(carry(result, mask));
        Register.V.set(overflow(a, b, result, mask));
        Register.Z.set(zero(result, mask));
        Register.N.set(negative(result, mask));
    }

    public static void daa() {
        var register = Register.A.value();
        int low = register & 0x0f;
        int result = register;
        int mask = Register.A.mask();

        if (low >= 0x0a || Register.H.isSet()) result += 0x06;
        if ((register >= 0x90 && low >= 0x0a) || register >= 0xa0 || Register.C.isSet()) result += 0x60;
        Register.A.value(result);

        Register.C.set(Register.C.isSet() || carry(result, mask));
        Register.V.clear();
        Register.Z.set(zero(result, mask));
        Register.N.set(negative(result, mask));
    }

    public static void add(Register register, BinaryOutput argument) {
        var a = register.value();
        var b = argument.value();
        var mask = register.mask();
        var result = a + b;
        register.value(result);

        if (mask == 0xff) Register.H.set(halfCarry(a, b, result));
        Register.C.set(carry(result, mask));
        Register.V.set(overflow(a, b, result, mask));
        Register.Z.set(zero(result, mask));
        Register.N.set(negative(result, mask));
    }

    public static void sbc(Register register, BinaryOutput argument) {
        var a = register.value();
        var b = argument.value();
        var c = Register.C.value();
        var mask = register.mask();
        var result = a - b - c;
        register.value(result);

        Register.C.set(carry(result, mask));
        Register.V.set(overflow(a, b, result, mask));
        Register.Z.set(zero(result, mask));
        Register.N.set(negative(result, mask));
    }

    public static void sub(Register register, BinaryOutput argument) {
        var a = register.value();
        var b = argument.value();
        var mask = register.mask();
        var result = a - b;
        register.value(result);

        Register.C.set(carry(result, mask));
        Register.V.set(overflow(a, b, result, mask));
        Register.Z.set(zero(result, mask));
        Register.N.set(negative(result, mask));
    }

    public static void increment(BinaryAccess argument) {
        var a = argument.value();
        var mask = argument.mask();
        var result = a + 1;
        argument.value(result);

        Register.V.set(result == BinaryOutput.highestBit(mask));
        Register.N.set(negative(result, mask));
        Register.Z.set(argument.isClear());
    }

    public static void decrement(BinaryAccess argument) {
        var mask = argument.mask();
        var a = argument.value();
        var result = a - 1;
        argument.value(result);

        Register.V.set(result == BinaryOutput.highestBit(mask) - 1);
        Register.N.set(negative(result, mask));
        Register.Z.set(argument.isClear());
    }

    private static boolean halfCarry(int a, int b, int result) {
        return ((a ^ b ^ result) & 0x10) == 0x10;
    }
}
