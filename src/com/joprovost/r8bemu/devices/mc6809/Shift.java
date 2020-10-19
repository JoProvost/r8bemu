package com.joprovost.r8bemu.devices.mc6809;

import com.joprovost.r8bemu.data.binary.BinaryAccess;

import static com.joprovost.r8bemu.data.binary.BinaryOutput.negative;
import static com.joprovost.r8bemu.data.transform.BinaryOutputSubset.bit;
import static com.joprovost.r8bemu.devices.mc6809.Check.carry;
import static com.joprovost.r8bemu.devices.mc6809.Check.overflow;
import static com.joprovost.r8bemu.devices.mc6809.Check.zero;

public class Shift {
    public static void lsl(BinaryAccess variable) {
        int before = variable.value();
        int result = before << 1;
        Register.N.set(negative(result, variable.mask()));
        Register.Z.set(zero(result, variable.mask()));
        Register.V.set(overflow(result, variable.mask()));
        Register.C.set(carry(result, variable.mask()));
        variable.value(result);
    }

    public static void lsr(BinaryAccess variable) {
        int before = variable.value();
        int result = before >> 1;
        Register.N.clear();
        Register.Z.set(zero(result, variable.mask()));
        Register.C.value(bit(0, before));
        variable.value(result);
    }

    public static void rol(BinaryAccess variable) {
        int before = variable.value();
        int result = (before << 1) | Register.C.value();
        Register.N.set(negative(result, variable.mask()));
        Register.Z.set(zero(result, variable.mask()));
        Register.C.set(carry(result, variable.mask()));
        Register.V.set(overflow(result, variable.mask()));
        variable.value(result);
    }

    public static void ror(BinaryAccess variable) {
        int before = variable.value();
        int result = (before >> 1) | (Register.C.value() << 7);
        Register.N.set(negative(result, variable.mask()));
        Register.Z.set(zero(result, variable.mask()));
        Register.C.value(bit(0, before));
        variable.value(result);
    }

    public static void asr(BinaryAccess variable) {
        int before = variable.value();
        int result = (before >> 1) | (before & 0x80);
        Register.N.set(negative(result, variable.mask()));
        Register.Z.set(zero(result, variable.mask()));
        Register.C.value(bit(0, before));
        variable.value(result);
    }

}
