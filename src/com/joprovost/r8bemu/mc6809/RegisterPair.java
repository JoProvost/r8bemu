package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.DataOutput;
import com.joprovost.r8bemu.data.Value;
import com.joprovost.r8bemu.data.transform.DataOutputSubset;

public class RegisterPair implements DataOutput {
    private final DataOutput registers;

    public RegisterPair(DataOutput registers) {
        this.registers = registers;
    }

    public static RegisterPair registers(DataOutput registers) {
        return new RegisterPair(registers);
    }

    private static DataAccess register(DataOutput registerCode) {
        switch (registerCode.value()) {
            case 0b0000: return Register.D;
            case 0b0001: return Register.X;
            case 0b0010: return Register.Y;
            case 0b0011: return Register.U;
            case 0b0100: return Register.S;
            case 0b0101: return Register.PC;
            case 0b1000: return Register.A;
            case 0b1001: return Register.B;
            case 0b1010: return Register.CC;
            case 0b1011: return Register.DP;
            default:
                // Other operand values result in the constant $FF being transferred; this
                // value is also used when there is an operation mixing 8 and 16 bit registers
                // (these effects are undocumented).
                // See https://techheap.packetizer.com/processors/6809/6809Instructions.html
                return DataAccess.of(Value.asByte(0xff));
        }
    }

    public DataAccess right() {
        return register(DataOutputSubset.of(this, 0b00001111));
    }

    public DataAccess left() {
        return register(DataOutputSubset.of(this, 0b11110000));
    }

    @Override
    public int value() {
        return registers.value();

    }

    @Override
    public int mask() {
        return registers.mask();
    }

    public String description() {
        return left().description() + "," + right().description();
    }

    public void exchange() {
        left().value(right().post(unused -> left()));
    }

    public void transfer() {
        right().value(left());
    }
}
