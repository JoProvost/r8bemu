package com.joprovost.r8bemu.memory;

import static com.joprovost.r8bemu.data.DataOutput.hex;

public class InvalidDevice implements MemoryMapped {
    final String name;

    public InvalidDevice(String name) {
        this.name = name;
    }

    @Override
    public int read(int address) {
        throw new UnsupportedOperationException(
                "Illegal read access on '" + name + "' to $" + hex(address, 0xffff));
    }

    @Override
    public void write(int address, int data) {
        throw new UnsupportedOperationException(
                "Illegal write access on '" + name + "' of $" + hex(data, 0xff) + " to $" + hex(address, 0xffff));
    }

    public static InvalidDevice invalid(String name) {
        return new InvalidDevice(name);
    }
}
