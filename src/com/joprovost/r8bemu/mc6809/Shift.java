package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.data.DataAccess;

import static com.joprovost.r8bemu.data.DataOutput.negative;
import static com.joprovost.r8bemu.data.DataOutputSubset.bit;

public class Shift {
    public static void lsl(DataAccess variable) {
        int before = variable.value();
        int result = before << 1;
        Register.N.set(negative(result, variable.mask()));
        Register.Z.set(result == 0);
        Register.V.value(bit(6, before) ^ bit(7, before));
        Register.C.value(bit(7, before));
        variable.value(result);
    }

    public static void lsr(DataAccess variable) {
        int before = variable.value();
        int result = before >> 1;
        Register.N.clear();
        Register.Z.set(result == 0);
        Register.C.value(bit(0, before));
        variable.value(result);
    }

    public static void rol(DataAccess variable) {
        int before = variable.value();
        int result = (before << 1) | Register.C.value();
        Register.N.set(negative(result, variable.mask()));
        Register.Z.set(result == 0);
        Register.C.value(bit(7, before));
        Register.V.value(bit(6, before) ^ bit(7, before));
        variable.value(result & variable.mask());
    }

    public static void ror(DataAccess variable) {
        int before = variable.value();
        int result = (before >> 1) | (Register.C.value() << 7);
        Register.N.set(negative(result, variable.mask()));
        Register.Z.set(result == 0);
        Register.C.value(bit(0, before));
        variable.value(result);
    }

    public static void asr(DataAccess variable) {
        int before = variable.value();
        int result = (before >> 1) | (before & 0x80);
        Register.N.set(negative(result, variable.mask()));
        Register.Z.set(result == 0);
        Register.C.value(bit(0, before));
        variable.value(result);
    }

}
