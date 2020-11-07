package com.joprovost.r8bemu;

import com.joprovost.r8bemu.data.Described;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

public class Disassembler extends ExecutionTrace {
    private final String[] code;
    private final long[] ticks;
    private final Path file;
    private long tick = 0;

    public Disassembler(Path file, int limit) {
        super(limit);
        this.file = file;
        code = new String[65536];
        ticks = new long[65536];
    }

    public void instruction(Described mnemonic) {
        super.instruction(mnemonic);
        ticks[address] = tick++;

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
    }
}
