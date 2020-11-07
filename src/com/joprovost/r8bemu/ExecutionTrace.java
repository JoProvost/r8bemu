package com.joprovost.r8bemu;

import com.joprovost.r8bemu.data.Described;
import com.joprovost.r8bemu.devices.mc6809.Debugger;

import java.util.ArrayDeque;

import static com.joprovost.r8bemu.data.binary.BinaryOutput.hex;

public class ExecutionTrace extends Debugger {
    public final int limit;
    protected final String[] label;
    private final ArrayDeque<String> trace = new ArrayDeque<>();

    public ExecutionTrace(int limit) {
        this.limit = limit;
        label = new String[65536];
    }

    public void label(String name, int address) {
        label[address] = name;
    }

    public void instruction(Described mnemonic) {
        if (isJump(mnemonic)) {
            label(hex(argument.value(), 0xffff), argument.value());
        }

        trace.addLast(column(8, label[address] != null ? label[address] + ":" : "") + column(8, hex(address, 0xffff)) + describe(mnemonic));
        if (trace.size() > limit) trace.removeFirst();
    }

    @Override
    public void onError(Exception e) {
        System.err.println("Execution Trace:");
        trace.forEach(System.err::println);

        System.err.println();
        e.printStackTrace();
    }
}
