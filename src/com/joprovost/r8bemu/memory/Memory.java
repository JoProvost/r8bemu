package com.joprovost.r8bemu.memory;

public class Memory implements MemoryMapped {
    private final byte[] bytes;
    private final int mask;

    public Memory(byte[] bytes) {
        this.bytes = bytes;
        this.mask = size(bytes.length) - 1;
    }

    public Memory(int size) {
        this(new byte[size(size)]);
    }

    public static Memory of(byte[] memory) {
        return new Memory(memory);
    }

    public static int size(int length) {
        var size = Integer.highestOneBit(length);
        size = size < length ? size * 2 : size;
        return size;
    }

    @Override
    public int read(int address) {
        return bytes[address & mask] & 0xff;
    }

    @Override
    public void write(int address, int data) {
        bytes[address & mask] = (byte) (data & 0xff);
    }
}
