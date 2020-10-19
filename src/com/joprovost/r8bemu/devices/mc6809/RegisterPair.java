package com.joprovost.r8bemu.devices.mc6809;

import com.joprovost.r8bemu.data.binary.BinaryAccess;
import com.joprovost.r8bemu.data.binary.BinaryOutput;
import com.joprovost.r8bemu.data.transform.BinaryOutputSubset;

public class RegisterPair implements BinaryOutput {
    private final BinaryOutput registers;

    public RegisterPair(BinaryOutput registers) {
        this.registers = registers;
    }

    public static RegisterPair registers(BinaryOutput registers) {
        return new RegisterPair(registers);
    }

    private static BinaryAccess register(BinaryOutput registerCode) {
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
                throw new UnsupportedOperationException("invalid register : " + registerCode.value());
        }
    }

    public BinaryAccess right() {
        return register(BinaryOutputSubset.of(this, 0b00001111));
    }

    public BinaryAccess left() {
        return register(BinaryOutputSubset.of(this, 0b11110000));
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
        // TODO Implement overflow as defined
        // When exchanging registers of different sizes, the 8-bit register is always exchanged with the lower half
        // of the 16-bit register, and the the upper half of the 16-bit register is then set to the value shown in
        // the table below.

        // Operand Order     8-bit Register Used     16-bit Register’s MSB after EXG
        //16 , 8             Any                     FF16 *
        //8 , 16             A or B                  FF16 *
        //8 , 16             CC or DP                Same as LSB

        if (right().mask() != left().mask())
            throw new UnsupportedOperationException("wrong sizes : " + left() + "," + right());

        left().value(right().post(unused -> left()));
    }

    public void transfer() {
        if (right().mask() != left().mask())
            throw new UnsupportedOperationException("wrong sizes : " + left() + "," + right());
        right().value(left());
    }
}
