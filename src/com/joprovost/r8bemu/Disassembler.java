package com.joprovost.r8bemu;

import com.joprovost.r8bemu.data.Described;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.joprovost.r8bemu.data.DataOutput.hex;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

class Disassembler extends Debugger {
    private final String[] code;
    private final boolean[] label;
    private final long[] ticks;
    private final Path file;
    private boolean isFirstInstruction = true;
    private long tick = 0;

    public Disassembler(Path file) {
        this.file = file;
        code = new String[65536];
        label = new boolean[65536];
        ticks = new long[65536];
    }

    public void instruction(Described mnemonic) {
        ticks[address] = tick++;

        if (isFirstInstruction) {
            label[address] = true;
            isFirstInstruction = false;
        }

        if (isJump(mnemonic)) {
            label[argument.unsigned()] = true;
        }

        if (code[address] == null) {
            code[address] = describe(mnemonic);

            try (OutputStream out = Files.newOutputStream(file, CREATE, TRUNCATE_EXISTING, WRITE)) {
                for (int addr = 0; addr < code.length; addr++) {
                    var line = code[addr];
                    if (line != null) {
                        if (label[addr]) out.write("\n".getBytes());
                        var address = label[addr] ? hex(addr, 0xffff) + ":" : "";
                        out.write((column(16, address) + line + ticks[addr] + "\n").getBytes());
                    }
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
