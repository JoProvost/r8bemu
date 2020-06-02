package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.data.DataAccess;

import static com.joprovost.r8bemu.data.DataOutput.negative;

public class Logic {
    public static void and(DataAccess register, DataAccess memory) {
        register.set(register.unsigned() & memory.unsigned());

        if (register != Register.CC) {
            Register.V.clear();
            Register.Z.set(register.isClear());
            Register.N.set(negative(register.unsigned(), register.mask()));
        }
    }

    public static void or(DataAccess register, DataAccess memory) {
        register.set(register.unsigned() | memory.unsigned());

        if (register != Register.CC) {
            Register.V.clear();
            Register.Z.set(register.isClear());
            Register.N.set(negative(register.unsigned(), register.mask()));
        }
    }

    public static void xor(Register register, DataAccess memory) {
        register.set(register.unsigned() ^ memory.unsigned());

        Register.V.clear();
        Register.Z.set(register.isClear());
        Register.N.set(negative(register.unsigned(), register.mask()));
    }

    public static void com(DataAccess argument) {
        argument.set(~argument.unsigned());

        Register.V.clear();
        Register.C.set();
        Register.Z.set(argument.isClear());
        Register.N.set(negative(argument.unsigned(), argument.mask()));
    }

    public static void bitTest(DataAccess register, DataAccess memory) {
        int result = register.unsigned() & memory.unsigned();
        Register.N.set(negative(result, register.mask()));
        Register.Z.set(result == 0);
        Register.V.clear();
    }
}
