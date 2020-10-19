package com.joprovost.r8bemu.devices.memory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class Memory implements Addressable {
    private final byte[] bytes;
    private final int mask;

    public Memory(byte[] bytes) {
        this.bytes = bytes;
        this.mask = size(bytes.length) - 1;
    }

    public Memory(int mask) {
        this(new byte[size(mask)]);
    }

    public static Memory of(byte[] memory) {
        return new Memory(memory);
    }

    public static int size(int length) {
        var size = Integer.highestOneBit(length);
        size = size < length ? size * 2 : size;
        return size;
    }

    public static Memory file(Path file) throws IOException {
        return new Memory(Files.readAllBytes(file));
    }

    @Override
    public int read(int address) {
        return bytes[address & mask] & 0xff;
    }

    @Override
    public void write(int address, int data) {
        bytes[address & mask] = (byte) (data & 0xff);
    }

    public void clear() {
        Arrays.fill(bytes, (byte) 0);
    }

    public int size() {
        return bytes.length;
    }
}
