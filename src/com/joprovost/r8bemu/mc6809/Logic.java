package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.DataOutput;

import static com.joprovost.r8bemu.data.DataOutput.negative;

public class Logic {
    public static void and(DataAccess register, DataOutput memory) {
        register.value(register.value() & memory.value());

        if (register != Register.CC) {
            Register.V.clear();
            Register.Z.set(register.isClear());
            Register.N.set(negative(register.value(), register.mask()));
        }
    }

    public static void or(DataAccess register, DataOutput memory) {
        register.value(register.value() | memory.value());

        if (register != Register.CC) {
            Register.V.clear();
            Register.Z.set(register.isClear());
            Register.N.set(negative(register.value(), register.mask()));
        }
    }

    public static void xor(Register register, DataOutput memory) {
        register.value(register.value() ^ memory.value());

        Register.V.clear();
        Register.Z.set(register.isClear());
        Register.N.set(negative(register.value(), register.mask()));
    }

    public static void complement(DataAccess argument) {
        argument.value(~argument.value());

        Register.V.clear();
        Register.C.set();
        Register.Z.set(argument.isClear());
        Register.N.set(negative(argument.value(), argument.mask()));
    }

    public static void bit(DataAccess register, DataAccess memory) {
        int result = register.value() & memory.value();
        Register.N.set(negative(result, register.mask()));
        Register.Z.set(result == 0);
        Register.V.clear();
    }

    public static void clear(DataAccess register) {
        register.clear();
        Register.N.clear();
        Register.Z.set();
        Register.V.clear();
        Register.C.clear();
    }
}
