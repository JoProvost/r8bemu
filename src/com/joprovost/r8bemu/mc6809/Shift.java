package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.data.DataAccess;

import static com.joprovost.r8bemu.data.DataOutput.negative;
import static com.joprovost.r8bemu.data.DataOutputSubset.bit;

public class Shift {
    public static void lsl(DataAccess variable) {
        int before = variable.unsigned();
        int result = before << 1;
        Register.N.set(negative(result, variable.mask()));
        Register.Z.set(result == 0);
        Register.V.set(bit(6, before) ^ bit(7, before));
        Register.C.set(bit(7, before));
        variable.set(result);
    }

    public static void lsr(DataAccess variable) {
        int before = variable.unsigned();
        int result = before >> 1;
        Register.N.clear();
        Register.Z.set(result == 0);
        Register.C.set(bit(0, before));
        variable.set(result);
    }

    public static void rol(DataAccess variable) {
        int before = variable.unsigned();
        int result = (before << 1) | Register.C.unsigned();
        Register.N.set(negative(result, variable.mask()));
        Register.Z.set(result == 0);
        Register.C.set(bit(7, before));
        Register.V.set(bit(6, before) ^ bit(7, before));
        variable.set(result & variable.mask());
    }

    public static void ror(DataAccess variable) {
        int before = variable.unsigned();
        int result = (before >> 1) | (Register.C.unsigned() << 7);
        Register.N.set(negative(result, variable.mask()));
        Register.Z.set(result == 0);
        Register.C.set(bit(0, before));
        variable.set(result);
    }

    public static void asr(DataAccess variable) {
        int before = variable.unsigned();
        int result = (before >> 1) | (before & 0x80);
        Register.N.set(negative(result, variable.mask()));
        Register.Z.set(result == 0);
        Register.C.set(bit(0, before));
        variable.set(result);
    }

}
