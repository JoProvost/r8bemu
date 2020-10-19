package com.joprovost.r8bemu.devices.mc6809;

import com.joprovost.r8bemu.data.binary.BinaryAccess;
import com.joprovost.r8bemu.data.binary.BinaryOutput;

import static com.joprovost.r8bemu.data.binary.BinaryOutput.negative;

public class Logic {
    public static void and(BinaryAccess register, BinaryOutput memory) {
        register.value(register.value() & memory.value());

        if (register != Register.CC) {
            Register.V.clear();
            Register.Z.set(register.isClear());
            Register.N.set(negative(register.value(), register.mask()));
        }
    }

    public static void or(BinaryAccess register, BinaryOutput memory) {
        register.value(register.value() | memory.value());

        if (register != Register.CC) {
            Register.V.clear();
            Register.Z.set(register.isClear());
            Register.N.set(negative(register.value(), register.mask()));
        }
    }

    public static void xor(Register register, BinaryOutput memory) {
        register.value(register.value() ^ memory.value());

        Register.V.clear();
        Register.Z.set(register.isClear());
        Register.N.set(negative(register.value(), register.mask()));
    }

    public static void complement(BinaryAccess argument) {
        argument.value(~argument.value());

        Register.V.clear();
        Register.C.set();
        Register.Z.set(argument.isClear());
        Register.N.set(negative(argument.value(), argument.mask()));
    }

    public static void bit(BinaryAccess register, BinaryAccess memory) {
        int result = register.value() & memory.value();
        Register.N.set(negative(result, register.mask()));
        Register.Z.set(result == 0);
        Register.V.clear();
    }

    public static void clear(BinaryAccess register) {
        register.clear();
        Register.N.clear();
        Register.Z.set();
        Register.V.clear();
        Register.C.clear();
    }
}
