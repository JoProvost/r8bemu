package com.joprovost.r8bemu.devices.memory;

import com.joprovost.r8bemu.data.binary.BinaryOutput;
import com.joprovost.r8bemu.data.discrete.DiscreteOutput;
import com.joprovost.r8bemu.data.discrete.Flag;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Represents a device directly connected on the address and data bus.
 */
public interface Addressable {
    static Addressable none() {
        return new Addressable() {
            @Override
            public int read(int address) {
                return 0;
            }

            @Override
            public void write(int address, int data) {
            }
        };
    }

    static Addressable when(DiscreteOutput select, Addressable... devices) {
        return new Addressable() {
            @Override
            public int read(int address) {
                int data = 0;
                if (select.isSet())
                    for (var device : devices)
                        data |= device.read(address);
                return data;
            }

            @Override
            public void write(int address, int data) {
                if (select.isSet()) for (var device : devices) device.write(address, data);
            }
        };
    }

    static Addressable select(int addr, int mask, Addressable... devices) {
        return new Addressable() {
            @Override
            public int read(int address) {
                int data = 0;
                if ((address & mask) == addr)
                    for (var device : devices)
                        data |= device.read(address);
                return data;
            }

            @Override
            public void write(int address, int data) {
                if ((address & mask) == addr) for (var device : devices) device.write(address, data);
            }
        };
    }

    static Addressable mask(int mask, Addressable... devices) {
        return new Addressable() {
            @Override
            public int read(int address) {
                address |= mask;
                int data = 0;
                for (var device : devices)
                    data |= device.read(address);
                return data;
            }

            @Override
            public void write(int address, int data) {
                address |= mask;
                for (var device : devices)
                    device.write(address, data);
            }
        };
    }

    /**
     * Helper method that connects multiple parallel devices on the same address and data bus.
     * All devices will receive read and write events. When reading, the data is "OR"ed between all devices thus
     * each parallel device must return "0" on unmapped addresses.
     */
    static Addressable bus(Addressable... devices) {
        return Addressable.when(Flag.value(true), devices);
    }

    /**
     * This kind of parallel peripheral discards write events, especially useful to represents ROM chips.
     */
    static Addressable readOnly(Addressable device) {
        return new Addressable() {
            @Override
            public int read(int address) {
                return device.read(address);
            }

            @Override
            public void write(int address, int data) {
            }
        };
    }

    static Addressable offset(Addressable device, BinaryOutput offset) {
        return new Addressable() {
            @Override
            public int read(int address) {
                return device.read(address + offset.value());
            }

            @Override
            public void write(int address, int data) {
                device.write(address + offset.value(), data);
            }
        };
    }


    static Optional<Addressable> rom(Path path) {
        if (!Files.exists(path)) return Optional.empty();
        try {
            return Optional.of(Addressable.readOnly(Memory.file(path)));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    int read(int address);

    void write(int address, int data);

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

    default int read(BinaryOutput address) {
        return read(address.value());
    }

    default void write(BinaryOutput address, int value) {
        write(address.value(), value);
    }
}
