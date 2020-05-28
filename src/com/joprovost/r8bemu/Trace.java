package com.joprovost.r8bemu;

import com.joprovost.r8bemu.data.Described;
import com.joprovost.r8bemu.mc6809.Register;

import java.util.ArrayDeque;
import java.util.Queue;

public class Trace extends Debugger {
    public static final int LOG_SIZE = 80;
    public Queue<String> log = new ArrayDeque<>(160);

    @Override
    public void log(String text) {
        if (log.size() == LOG_SIZE) log.remove();
        log.add(text);
    }

    public void instruction(Described mnemonic) {
        log(String.join(
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
        ));
    }

    public String last() {
        var last = log.peek();
        return (last != null) ? last : "";
    }
}
