package com.joprovost.r8bemu;

import com.joprovost.r8bemu.data.Described;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

class Disassembler extends Debugger {
    private final String[] code;
    private final Path file;

    public Disassembler(Path file) {
        this.file = file;
        code = new String[65536];
    }

    @Override
    public void log(String text) {
    }

    public void instruction(Described mnemonic) {
        if (code[address] == null) {
            code[address] = describe(mnemonic);

            try (OutputStream out = Files.newOutputStream(file, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
                for (var line : code) if (line != null) out.write((line + "\n").getBytes());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

}
