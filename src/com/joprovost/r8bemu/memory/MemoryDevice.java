package com.joprovost.r8bemu.memory;

import com.joprovost.r8bemu.data.BitOutput;
import com.joprovost.r8bemu.data.DataOutput;

/**
 * Represents a device directly connected on the address and data bus.
 */
public interface MemoryDevice {
    static MemoryDevice none() {
        return new MemoryDevice() {
            @Override
            public int read(int address) {
                return 0;
            }

            @Override
            public void write(int address, int data) {
            }
        };
    }

    static MemoryDevice when(BitOutput cs, MemoryDevice device) {
        return new MemoryDevice() {

            @Override
            public int read(int address) {
                if (cs.isClear()) return 0;
                return device.read(address);
            }

            @Override
            public void write(int address, int data) {
                if (cs.isClear()) return;
                device.write(address, data);
            }
        };
    }

    static MemoryDevice pageSwitchOn(BitOutput pageSwitch, int mask, MemoryDevice device) {
        return new MemoryDevice() {

            @Override
            public int read(int address) {
                if (pageSwitch.isSet()) address |= mask;
                return device.read(address);
            }

            @Override
            public void write(int address, int data) {
                if (pageSwitch.isSet()) address |= mask;
                device.write(address, data);
            }
        };
    }

    /**
     * Helper method that connects multiple parallel devices on the same address and data bus.
     * All devices will receive read and write events. When reading, the data is "OR"ed between all devices thus
     * each parallel device must return "0" on unmapped addresses.
     */
    static MemoryDevice bus(MemoryDevice... devices) {
        return new MemoryDevice() {
            @Override
            public int read(int address) {
                int data = 0;
                for (var device : devices) {
                    data |= device.read(address);
                }
                return data;
            }

            @Override
            public void write(int address, int data) {
                for (var device : devices) {
                    device.write(address, data);
                }
            }
        };
    }

    /**
     * This kind of parallel peripheral discards write events, especially useful to represents ROM chips.
     */
    static MemoryDevice readOnly(MemoryDevice device) {
        return new MemoryDevice() {
            @Override
            public int read(int address) {
                return device.read(address);
            }

            @Override
            public void write(int address, int data) {
            }
        };
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

    default int read(DataOutput address) {
        return read(address.value());
    }

    default void write(DataOutput address, int value) {
        write(address.value(), value);
    }
}
