package com.joprovost.r8bemu.memory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This kind of parallel peripheral discards write events, especially useful to represents ROM chips.
 */
public class ReadOnly implements MemoryMapped {
    private final MemoryMapped device;

    public ReadOnly(MemoryMapped device) {
        this.device = device;
    }

    public static ReadOnly readOnly(MemoryMapped peripheral) {
        return new ReadOnly(peripheral);
    }

    public static MemoryMapped file(Path file) throws IOException {
        byte[] data = Files.readAllBytes(file);
        return readOnly(new Memory(data));
    }

    @Override
    public int read(int address) {
        return device.read(address);
    }

    @Override
    public void write(int address, int data) {
    }
}
