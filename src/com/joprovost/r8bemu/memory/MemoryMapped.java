package com.joprovost.r8bemu.memory;

import com.joprovost.r8bemu.data.DataOutput;

/**
 * Represents a device directly connected on the address and data bus.
 */
public interface MemoryMapped {
    default int read(int address) {
        return 0;
    }

    default void write(int address, int data) {
    }

    default void write(int address, int data, int... next) {
        write(address, data);
        for (int i = 0; i < next.length; i++) {
            write(address + i + 1, next[i]);
        }
    }

    default void write(int address, byte[] data) {
        for (int i = 0; i < data.length; i++) {
            write(address + i, data[i]);
        }
    }

    default int read(DataOutput address) {
        return read(address.unsigned());
    }

    default void write(DataOutput address, int value) {
        write(address.unsigned(), value);
    }
}
