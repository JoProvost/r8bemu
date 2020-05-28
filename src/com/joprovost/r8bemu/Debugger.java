package com.joprovost.r8bemu;

import com.joprovost.r8bemu.data.Described;
import com.joprovost.r8bemu.mc6809.Register;

import java.nio.file.Path;

public abstract class Debugger {

    protected int address;
    protected Described parameter;

    public static Trace trace() {
        return new Trace();
    }

    public static Debugger disassembler(Path file) {
        return new Disassembler(file);
    }

    public static Debugger none() {
        return new Debugger() {
            @Override
            public void log(String text) {

            }

            @Override
            public void instruction(Described mnemonic) {

            }
        };
    }

    public abstract void log(String text);

    public void at(int address) {
        this.address = address;
        this.parameter = Described.EMPTY;
    }

    public abstract void instruction(Described mnemonic);

    protected String leftPad(String padding, String string) {
        return padding.substring(string.length()) + string;
    }

    protected String column(int size, Object string) {
        return string + " ".repeat(size - string.toString().length());
    }

    public <T extends Described> T parameter(T data) {
        parameter = data;
        return data;
    }

    protected String describe(Described mnemonic) {
        return String.join(
                "",
                column(16, leftPad("0000", Integer.toHexString(address)) + " :"),
                column(8, mnemonic.description()),
                column(24, parameter.description()),
                "; ",
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
}
