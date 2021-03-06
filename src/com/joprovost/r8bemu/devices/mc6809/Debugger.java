package com.joprovost.r8bemu.devices.mc6809;

import com.joprovost.r8bemu.data.Described;
import com.joprovost.r8bemu.data.binary.BinaryOutput;

public abstract class Debugger {

    protected int address;
    protected BinaryOutput argument;

    public static Debugger none() {
        return new Debugger() {

            @Override
            public void instruction(Described mnemonic) {

            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        };
    }

    public void at(int address) {
        this.address = address;
        this.argument = BinaryOutput.NONE;
    }

    public void label(String name, int address) {

    }

    public abstract void instruction(Described mnemonic);

    public abstract void onError(Exception e);

    protected String column(int size, Object string) {
        return string + " ".repeat(size - string.toString().length());
    }

    public <T extends BinaryOutput> T argument(T argument) {
        this.argument = argument;
        return argument;
    }

    protected String describe(Described mnemonic) {
        return String.join(
                "",
                column(8, mnemonic.description()),
                column(24, argument.description() + (isJump(mnemonic) ? " ;" + argument.hex() : "")),
                "; ",
                column(12, Register.PC),
                column(12, Register.A),
                column(12, Register.B),
                column(12, Register.D),
                column(12, Register.X),
                column(12, Register.Y),
                column(12, Register.U),
                column(12, Register.S),
                column(12, Register.DP),
                column(12, Register.CC)
        );
    }

    protected boolean isJump(Described mnemonic) {
        switch (mnemonic.description()) {
            case "BCC": case "BCS": case "BEQ": case "BGE": case "BGT": case "BHI": case "BLE": case "BLS": case "BLT":
            case "BMI": case "BNE": case "BPL": case "BRA": case "BRN": case "BSR": case "BVC": case "BVS":
            case "LBCC": case "LBCS": case "LBEQ": case "LBGE": case "LBGT": case "LBHI": case "LBLE": case "LBLS":
            case "LBLT": case "LBMI": case "LBNE": case "LBPL": case "LBRA": case "LBRN": case "LBSR": case "LBVC":
            case "LBVS": case "JMP": case "JSR":
                return true;
            default:
                return false;
        }
    }
}
