package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.Described;
import com.joprovost.r8bemu.data.Subset;

public class Pair implements Described {
    public final DataAccess left;
    public final DataAccess right;

    public Pair(DataAccess left, DataAccess right) {
        this.left = left;
        this.right = right;
    }

    public static Pair registers(DataAccess registers) {
        return new Pair(
                register(Subset.of(registers, 0b11110000)),
                register(Subset.of(registers, 0b00001111))
        );
    }

    private static DataAccess register(Subset registerCode) {
        switch (registerCode.unsigned()) {
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
                throw new UnsupportedOperationException("Unknown register code: 0b" + Integer.toBinaryString(registerCode.unsigned()));
        }
    }

    public String description() {
        return left.description() + "," + right.description();
    }

    public void exchange() {
        left.set(right.post(unused -> left));
    }

    public void transfer() {
        right.set(left);
    }
}
