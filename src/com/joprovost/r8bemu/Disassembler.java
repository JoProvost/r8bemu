package com.joprovost.r8bemu;

import com.joprovost.r8bemu.data.Described;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;

import static com.joprovost.r8bemu.data.DataOutput.hex;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

class Disassembler extends Debugger {
    private static final boolean DEBUG = false;
    private final String[] code;
    private final String[] label;
    private final long[] ticks;
    private final Path file;
    private long tick = 0;
    private ArrayDeque<String> stack = new ArrayDeque<>();

    public Disassembler(Path file, int reset, int irq, int firq, int nmi) {
        this.file = file;
        code = new String[65536];
        label = new String[65536];
        ticks = new long[65536];

        label[reset] = "RESET";
        label[irq] = "IRQ";
        label[firq] = "FIRQ";
        label[nmi] = "NMI";
    }

    public void instruction(Described mnemonic) {
        ticks[address] = tick++;

        if (isJump(mnemonic)) {
            label[argument.value()] = hex(argument.value(), 0xffff);
        }

        if (code[address] == null) {
            code[address] = describe(mnemonic);

            try (OutputStream out = Files.newOutputStream(file, CREATE, TRUNCATE_EXISTING, WRITE)) {
                for (int addr = 0; addr < code.length; addr++) {
                    var line = code[addr];
                    if (line != null) {
                        if (label[addr] != null) {
                            out.write("\n".getBytes());
                            out.write((column(16, label[addr] + ":") + line + ticks[addr] + "\n").getBytes());
                        } else {
                            out.write((column(16, "") + line + ticks[addr] + "\n").getBytes());
                        }
                    }
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        if (DEBUG) {
            stack.addLast(column(16, label[address] != null ? label[address] + ":" : "") + describe(mnemonic));
            if (stack.size() > 80) stack.removeFirst();
        }
    }
}
